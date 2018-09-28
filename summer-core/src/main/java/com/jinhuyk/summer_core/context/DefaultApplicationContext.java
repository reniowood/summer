package com.jinhuyk.summer_core.context;

import com.google.common.collect.ImmutableMap;
import com.jinhuyk.summer_core.annotations.Autowired;
import com.jinhuyk.summer_core.annotations.Component;
import com.jinhuyk.summer_core.annotations.Configuration;
import com.jinhuyk.summer_core.components.*;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class DefaultApplicationContext implements ApplicationContext {
    private final String basePackageName;

    private Map<String, AbstractComponent<?>> nameComponentMap = new HashMap<>();
    private Map<Class<?>, AbstractComponent<?>> classComponentMap = new HashMap<>();

    private DefaultApplicationContext(Class applicationClass) {
        this.basePackageName = applicationClass.getPackage().getName();
    }

    public static ApplicationContext newContext(Class applicationClass) {
        DefaultApplicationContext applicationContext = new DefaultApplicationContext(applicationClass);

        applicationContext.scanComponents();
        applicationContext.injectDependencies();

        return applicationContext;
    }

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

    private void scanComponents() {
        scanComponentAnnotatedClasses();
        scanConfigurations();
        findDependencies();
        createComponentsWithDependencies();
    }

    private void scanComponentAnnotatedClasses() {
        Reflections reflections = new Reflections(this.basePackageName);

        for (Class<?> componentClass : reflections.getTypesAnnotatedWith(Component.class)) {
            String componentName = getComponentNameFromComponentAnnotation(componentClass);

            if (nameComponentMap.containsKey(componentName)) {
                throw new RuntimeException(String.format("Application has nameComponentMap with duplicate names: %s", componentName));
            }

            createConstructorCreatableComponent(componentName, componentClass);
        }
    }

    private void scanConfigurations() {
        Reflections reflections = new Reflections(this.basePackageName);

        for (Class<?> configurationClass : reflections.getTypesAnnotatedWith(Configuration.class)) {
            String configurationName = configurationClass.getSimpleName();

            if (nameComponentMap.containsKey(configurationName)) {
                throw new RuntimeException(String.format("Application has nameComponentMap with duplicate names: %s", configurationName));
            }

            AbstractComponent<?> configurationComponent = createConstructorCreatableComponent(configurationName, configurationClass);
            scanBeans(configurationClass, configurationComponent);
        }
    }

    private void scanBeans(Class<?> configurationClass, AbstractComponent<?> configurationComponent) {
        for (Method method : configurationClass.getMethods()) {
            if (method.isAnnotationPresent(Component.class)) {
                Component componentAnnotation = method.getAnnotation(Component.class);

                String componentName = getComponentNameFromComponentAnnotation(componentAnnotation.getClass());

                if (nameComponentMap.containsKey(componentName)) {
                    throw new RuntimeException(String.format("Application has nameComponentMap with duplicate names: %s", componentName));
                }

                createMethodCreatableComponent(componentName, method, configurationComponent);
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

    private void injectDependencies() {
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

    private String getComponentNameFromComponentAnnotation(Class<?> aClass) {
        Component componentAnnotation = aClass.getAnnotation(Component.class);
        return componentAnnotation.name().isEmpty() ? aClass.getSimpleName() : componentAnnotation.name();
    }
}
