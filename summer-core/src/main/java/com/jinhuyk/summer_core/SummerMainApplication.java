package com.jinhuyk.summer_core;

import com.jinhuyk.summer_core.annotations.Autowired;
import com.jinhuyk.summer_core.annotations.Component;
import com.jinhuyk.summer_core.annotations.SummerApplication;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SummerMainApplication {
    private static Map<String, Object> components = new ConcurrentHashMap<>();
    private static Map<Class<?>, String> classNameMap = new ConcurrentHashMap<>();
    private static Map<String, Class<?>> nameClassMap = new ConcurrentHashMap<>();

    public static void init() {
        components.clear();
        classNameMap.clear();
        nameClassMap.clear();
    }

    public static void run(Class applicationClass) {
        if (!hasSummerApplicationAnnotation(applicationClass)) {
            throw new RuntimeException("This summer application class doesn't have @SummerApplication annotation");
        }

        scanComponents(applicationClass);
        injectDependencies();
    }

    public static Object getComponent(String name) {
        if (components.containsKey(name)) {
            return components.get(name);
        } else {
            throw new RuntimeException(String.format("Component %s is not found.", name));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getComponent(Class<T> tClass) {
        if (classNameMap.containsKey(tClass)) {
            return (T) getComponent(classNameMap.get(tClass));
        } else {
            throw new RuntimeException(String.format("Component %s is not found.", tClass.getName()));
        }
    }

    private static boolean hasSummerApplicationAnnotation(Class applicationClass) {
        for (Annotation annotation : applicationClass.getAnnotations()) {
            if (annotation.annotationType().equals(SummerApplication.class)) {
                return true;
            }
        }

        return false;
    }

    private static void scanComponents(Class<?> applicationClass) {
        scanComponentAnnotatedClasses(applicationClass);

        Map<String, List<String>> dependencyGraph = new HashMap<>();
        Map<String, List<String>> reverseDependencyGraph = new HashMap<>();
        Map<String, Constructor<?>> constructorMap = new HashMap<>();
        Map<String, List<String>> parametersMap = new HashMap<>();
        for (String componentName : nameClassMap.keySet()) {
            dependencyGraph.put(componentName, new ArrayList<>());
            reverseDependencyGraph.put(componentName, new ArrayList<>());
        }

        createDependencyGraph(dependencyGraph, reverseDependencyGraph, constructorMap, parametersMap);
        createComponentsWithDependencies(dependencyGraph, reverseDependencyGraph, constructorMap, parametersMap);
    }

    private static void scanComponentAnnotatedClasses(Class<?> applicationClass) {
        Package basePackage = applicationClass.getPackage();
        String backPackageName = basePackage.getName();

        Reflections reflections = new Reflections(backPackageName);
        for (Class<?> componentClass : reflections.getTypesAnnotatedWith(Component.class)) {
            String componentName = getComponentNameFromComponentAnnotation(componentClass);

            if (nameClassMap.containsKey(componentName)) {
                throw new RuntimeException(String.format("Application has components with duplicate names: %s", componentName));
            }

            nameClassMap.put(componentName, componentClass);
            classNameMap.put(componentClass, componentName);
        }
    }

    private static void createDependencyGraph(Map<String, List<String>> dependencyGraph,
                                              Map<String, List<String>> reverseDependencyGraph,
                                              Map<String, Constructor<?>> constructorMap,
                                              Map<String, List<String>> parametersMap) {
        for (String componentName : nameClassMap.keySet()) {
            Class<?> componentClass = nameClassMap.get(componentName);

            if (componentClass.getConstructors().length == 0) {
                throw new RuntimeException(String.format("Component %s doesn't have any public constructor.", componentName));
            } else {
                if (hasMoreThanOneAutowiredConstructor(componentClass)) {
                    throw new RuntimeException(String.format("Component %s has more than one public constructor with @Autowired.", componentName));
                }

                List<Constructor<?>> constructors = getAutowiredConstructors(componentClass);

                for (Constructor<?> constructor : constructors) {
                    List<String> parameters = new ArrayList<>();

                    for (Parameter parameter : constructor.getParameters()) {
                        Class<?> parameterClass = parameter.getType();

                        if (classNameMap.containsKey(parameterClass)) {
                            String parameterComponentName = classNameMap.get(parameterClass);
                            parameters.add(parameterComponentName);

                            List<String> dependencies = dependencyGraph.get(componentName);
                            dependencies.add(parameterComponentName);
                            dependencyGraph.put(componentName, dependencies);

                            List<String> reverseDependencies = reverseDependencyGraph.get(parameterComponentName);
                            reverseDependencies.add(componentName);
                            reverseDependencyGraph.put(parameterComponentName, reverseDependencies);
                        } else {
                            throw new RuntimeException(String.format("Component %s has a non-component parameter within constructor: %s", componentName, parameter.getType().getSimpleName()));
                        }
                    }

                    constructorMap.put(componentName, constructor);
                    parametersMap.put(componentName, parameters);
                }

                for (Field field : componentClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Autowired.class)) {
                        Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
                        String dependentComponentName = autowiredAnnotation.name().isEmpty() ? field.getType().getSimpleName() : autowiredAnnotation.name();

                        if (nameClassMap.containsKey(dependentComponentName)) {
                            List<String> dependencies = dependencyGraph.get(dependentComponentName);
                            dependencies.add(componentName);
                            dependencyGraph.put(dependentComponentName, dependencies);

                            List<String> reverseDependencies = reverseDependencyGraph.get(componentName);
                            reverseDependencies.add(dependentComponentName);
                            reverseDependencyGraph.put(componentName, reverseDependencies);
                        } else {
                            throw new RuntimeException(String.format("Component %s has a non-component field: %s", componentName, dependentComponentName));
                        }
                    }
                }
            }
        }
    }

    private static List<Constructor<?>> getAutowiredConstructors(Class<?> componentClass) {
        Constructor<?>[] constructors = componentClass.getConstructors();

        if (constructors.length == 1) {
            return Collections.singletonList(constructors[0]);
        }

        List<Constructor<?>> autowiredConstructors = new ArrayList<>();

        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                autowiredConstructors.add(constructor);
            }
        }

        return autowiredConstructors;
    }

    private static boolean hasMoreThanOneAutowiredConstructor(Class<?> componentClass) {
        int numAutowiredConstructors = 0;

        for (Constructor<?> constructor : componentClass.getConstructors()) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                numAutowiredConstructors++;
            }
        }

        return numAutowiredConstructors > 1;
    }

    private static void createComponentsWithDependencies(Map<String, List<String>> dependencyGraph,
                                                         Map<String, List<String>> reverseDependencyGraph,
                                                         Map<String, Constructor<?>> constructorMap,
                                                         Map<String, List<String>> parametersMap) {
        Queue<String> queue = new LinkedList<>();
        Set<String> unresolvedComponents = new HashSet<>(dependencyGraph.keySet());
        Map<String, Integer> numDependenciesMap = new HashMap<>();

        for (String componentName : dependencyGraph.keySet()) {
            int numDependencies = dependencyGraph.get(componentName).size();

            numDependenciesMap.put(componentName, numDependencies);

            if (numDependencies == 0) {
                queue.offer(componentName);
                unresolvedComponents.remove(componentName);

                Constructor<?> constructor = constructorMap.get(componentName);
                components.put(componentName, createInstance(componentName, constructor));
            }
        }

        while (!queue.isEmpty()) {
            String componentName = queue.poll();

            List<String> reverseDependencies = reverseDependencyGraph.get(componentName);

            for (String dependentComponentName : reverseDependencies) {
                numDependenciesMap.put(dependentComponentName, numDependenciesMap.get(dependentComponentName) - 1);

                if (numDependenciesMap.get(dependentComponentName) == 0) {
                    queue.offer(dependentComponentName);
                    unresolvedComponents.remove(dependentComponentName);

                    Constructor<?> constructor = constructorMap.get(dependentComponentName);
                    List<String> parameters = parametersMap.get(dependentComponentName);
                    components.put(dependentComponentName, createInstance(dependentComponentName, constructor, parameters.toArray(new String[0])));
                }
            }
        }

        if (!unresolvedComponents.isEmpty()) {
            throw new RuntimeException(String.format("There are dependency cycles: %s", unresolvedComponents));
        }
    }

    private static Object createInstance(String componentName, Constructor<?> constructor, String... parameterComponentNames
    ) {
        Object[] parameters = new Object[parameterComponentNames.length];

        for (int i = 0; i < parameterComponentNames.length; ++i) {
            String parameterComponentName = parameterComponentNames[i];

            if (components.containsKey(parameterComponentName)) {
                parameters[i] = components.get(parameterComponentName);
            } else {
                throw new RuntimeException(String.format("Component %s has unresolved parameter %s.", componentName, parameterComponentName));
            }
        }

        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException e) {
            throw new RuntimeException(String.format("Component %s cannot be instantiated with %s.", componentName, constructor));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Application doesn't have access to %s of component %s.", constructor, componentName));
        } catch (InvocationTargetException e) {
            throw new RuntimeException(String.format("Application couldn't instantiate component %s with %s.", componentName, constructor));
        }
    }

    private static void injectDependencies() {
        for (String componentName : components.keySet()) {
            Object component = components.get(componentName);
            Class<?> componentClass = component.getClass();

            for (Field field : componentClass.getDeclaredFields()) {
                Autowired autowired = field.getAnnotation(Autowired.class);

                if (autowired != null) {
                    Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
                    String injectedComponentName = autowiredAnnotation.name().isEmpty() ? field.getType().getSimpleName() : autowiredAnnotation.name();

                    if (components.containsKey(injectedComponentName)) {
                        Object injectedComponent = components.get(injectedComponentName);

                        try {
                            field.setAccessible(true);
                            field.set(component, injectedComponent);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(String.format("Component %s is not injectable.", injectedComponentName));
                        }
                    } else {
                        throw new RuntimeException(String.format("Component %s doesn't exist.", injectedComponentName));
                    }
                }
            }
        }
    }

    private static String getComponentNameFromComponentAnnotation(Class<?> aClass) {
        Component componentAnnotation = aClass.getAnnotation(Component.class);
        return componentAnnotation.name().isEmpty() ? aClass.getSimpleName() : componentAnnotation.name();
    }
}
