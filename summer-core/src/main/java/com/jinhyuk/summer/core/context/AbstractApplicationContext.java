package com.jinhyuk.summer.core.context;

import com.google.common.collect.ImmutableMap;
import com.jinhyuk.summer.core.annotations.AnnotationUtils;
import com.jinhyuk.summer.core.annotations.Autowired;
import com.jinhyuk.summer.core.annotations.Component;
import com.jinhyuk.summer.core.components.AbstractComponent;
import com.jinhyuk.summer.core.components.ConstructorCreatableComponent;
import com.jinhyuk.summer.core.components.Dependent;
import com.jinhyuk.summer.core.components.MethodCreatableComponent;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractApplicationContext implements ApplicationContext {
    private Map<String, AbstractComponent<?>> nameComponentMap = new HashMap<>();
    private Map<Class<?>, AbstractComponent<?>> classComponentMap = new HashMap<>();

    @Override
    public Object getComponent(String name) {
        if (nameComponentMap.containsKey(name)) {
            return nameComponentMap.get(name);
        } else {
            throw new RuntimeException(String.format("AbstractComponent %s is not found.", name));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getComponent(Class<T> tClass) {
        if (classComponentMap.containsKey(tClass)) {
            return (T) classComponentMap.get(tClass).getObject();
        } else {
            throw new RuntimeException(String.format("AbstractComponent %s is not found.", tClass.getName()));
        }
    }

    @Override
    public Map<String, Object> getComponents() {
        Map<String, Object> components = new HashMap<>();

        for (String name : nameComponentMap.keySet()) {
            AbstractComponent<?> component = nameComponentMap.get(name);

            components.put(name, component.getObject());
        }

        return ImmutableMap.copyOf(components);
    }

    protected AbstractApplicationContext(String basePackageName) {
        scanComponents(basePackageName);
        injectDependencies();
    }

    protected void scanComponents(String basePackageName) {
        scanComponentAnnotatedClasses(basePackageName);
        findDependencies();
        createComponentsWithDependencies();
    }

    protected void injectDependencies() {
        for (String componentName : nameComponentMap.keySet()) {
            AbstractComponent<?> component = nameComponentMap.get(componentName);
            Object componentObject = component.getObject();
            Class<?> componentClass = componentObject.getClass();

            for (Field field : componentClass.getDeclaredFields()) {
                Autowired autowired = field.getAnnotation(Autowired.class);

                if (autowired != null) {
                    Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
                    String injectedComponentName = autowiredAnnotation.name().isEmpty() ? field.getType().getSimpleName() : autowiredAnnotation.name();

                    if (nameComponentMap.containsKey(injectedComponentName)) {
                        AbstractComponent<?> injectedComponent = nameComponentMap.get(injectedComponentName);

                        try {
                            field.setAccessible(true);
                            field.set(componentObject, injectedComponent.getObject());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(String.format("AbstractComponent %s is not injectable.", injectedComponentName));
                        }
                    } else {
                        throw new RuntimeException(String.format("AbstractComponent %s doesn't exist.", injectedComponentName));
                    }
                }
            }
        }
    }

    private void scanComponentAnnotatedClasses(String basePackageName) {
        Reflections reflections = new Reflections(basePackageName, new SubTypesScanner(false));

        for (Class<?> aClass : reflections.getSubTypesOf(Object.class)) {
            if (AnnotationUtils.findAnnotation(aClass, Component.class) != null) {
                if (!aClass.isAnnotation()) {
                    String componentName = getComponentNameFromComponentAnnotation(aClass);

                    if (nameComponentMap.containsKey(componentName)) {
                        throw new RuntimeException(String.format("Application has nameComponentMap with duplicate names: %s", componentName));
                    }

                    createConstructorCreatableComponent(componentName, aClass);

                    AbstractComponent<?> configurationComponent = createConstructorCreatableComponent(componentName, aClass);
                    scanBeans(aClass, configurationComponent);
                }
            }
        }
    }

    private void scanBeans(Class<?> componentClass, AbstractComponent<?> component) {
        for (Method method : componentClass.getMethods()) {
            if (method.isAnnotationPresent(Component.class)) {
                Component componentAnnotation = method.getAnnotation(Component.class);

                String componentName = getComponentNameFromComponentAnnotation(componentAnnotation.getClass());

                if (nameComponentMap.containsKey(componentName)) {
                    throw new RuntimeException(String.format("Application has nameComponentMap with duplicate names: %s", componentName));
                }

                createMethodCreatableComponent(componentName, method, component);
            }
        }
    }

    private ConstructorCreatableComponent<?> createConstructorCreatableComponent(String componentName, Class<?> componentClass) {
        ConstructorCreatableComponent<?> component = new ConstructorCreatableComponent<>(componentClass, componentName);
        nameComponentMap.put(componentName, component);
        classComponentMap.put(componentClass, component);
        return component;
    }

    private MethodCreatableComponent<?> createMethodCreatableComponent(String componentName, Method method, AbstractComponent<?> configurationComponent) {
        MethodCreatableComponent<?> component = new MethodCreatableComponent<>(method.getReturnType(), componentName, method, configurationComponent);
        nameComponentMap.put(componentName, component);
        classComponentMap.put(method.getReturnType(), component);
        return component;
    }

    private void findDependencies() {
        for (String componentName : nameComponentMap.keySet()) {
            AbstractComponent<?> component = nameComponentMap.get(componentName);

            component.findDependencies(nameComponentMap);
        }
    }

    private void createComponentsWithDependencies() {
        Queue<String> queue = new LinkedList<>();
        Set<String> unresolvedComponents = new HashSet<>(nameComponentMap.keySet());
        Map<String, Integer> numDependenciesMap = new HashMap<>();

        for (String componentName : nameComponentMap.keySet()) {
            AbstractComponent<?> component = nameComponentMap.get(componentName);

            numDependenciesMap.put(componentName, component.getNumDependencies());

            if (component.hasNoDependency()) {
                queue.offer(componentName);
                unresolvedComponents.remove(componentName);

                component.create();
            }
        }

        while (!queue.isEmpty()) {
            String componentName = queue.poll();
            AbstractComponent<?> component = nameComponentMap.get(componentName);

            for (Dependent dependency : component.getInjectedDependencies()) {
                String dependentComponentName = dependency.getName();

                numDependenciesMap.put(dependentComponentName, numDependenciesMap.get(dependentComponentName) - 1);

                if (numDependenciesMap.get(dependentComponentName) == 0) {
                    queue.offer(dependentComponentName);
                    unresolvedComponents.remove(dependentComponentName);

                    AbstractComponent<?> dependentComponent = nameComponentMap.get(dependentComponentName);
                    dependentComponent.create();
                }
            }
        }

        if (!unresolvedComponents.isEmpty()) {
            throw new RuntimeException(String.format("There are dependency cycles: %s", unresolvedComponents));
        }
    }

    private String getComponentNameFromComponentAnnotation(Class<?> aClass) {
        Component component = AnnotationUtils.findAnnotation(aClass, Component.class);

        if (component.name().isEmpty()) {
            return aClass.getSimpleName();
        } else {
            return component.name();
        }
    }
}
