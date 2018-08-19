package com.jinhuyk.summer_core;

import com.jinhuyk.summer_core.annotations.Component;
import com.jinhuyk.summer_core.annotations.SummerApplication;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SummerMainApplication {
    private static Map<String, Object> components = new ConcurrentHashMap<>();

    public static void run(Class applicationClass) {
        if (!hasSummerApplicationAnnotation(applicationClass)) {
            throw new RuntimeException("This summer application class doesn't have @SummerApplication annotation");
        }

        scanComponents(applicationClass);
    }

    private static boolean hasSummerApplicationAnnotation(Class applicationClass) {
        for (Annotation annotation : applicationClass.getAnnotations()) {
            if (annotation.annotationType().equals(SummerApplication.class)) {
                return true;
            }
        }

        return false;
    }

    private static void scanComponents(Class applicationClass) {
        Package basePackage = applicationClass.getPackage();
        String backPackageName = basePackage.getName();

        Reflections reflections = new Reflections(backPackageName);
        for (Class<?> aClass : reflections.getTypesAnnotatedWith(Component.class)) {
            String componentName = getComponentName(aClass);

            if (components.containsKey(componentName)) {
                throw new RuntimeException(String.format("Application has components with duplicate names: %s", componentName));
            }

            try {
                Constructor<?> constructor = aClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                components.put(componentName, constructor.newInstance());
            } catch (InstantiationException e) {
                throw new RuntimeException(String.format("Component %s cannot be initiated.", componentName));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(String.format("Application doesn't have access to component %s to be initiated", componentName));
            } catch (InvocationTargetException e) {
                throw new RuntimeException(String.format("Constructor of component %s is not invokable", componentName));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(String.format("Component %s has no default constructor", componentName));
            }
        }
    }

    private static String getComponentName(Class<?> aClass) {
        Component componentAnnotation = aClass.getAnnotation(Component.class);
        return componentAnnotation.name().isEmpty() ? aClass.getSimpleName() : componentAnnotation.name();
    }
}
