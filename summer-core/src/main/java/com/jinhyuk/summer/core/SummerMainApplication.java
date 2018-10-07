package com.jinhyuk.summer.core;

import com.jinhyuk.summer.core.annotations.SummerApplication;
import com.jinhyuk.summer.core.context.ApplicationContext;
import com.jinhyuk.summer.core.context.DefaultApplicationContext;

public class SummerMainApplication {
    public static ApplicationContext run(Class<?> applicationClass) {
        if (!hasSummerApplicationAnnotation(applicationClass)) {
            throw new RuntimeException("This summer application class doesn't have @SummerApplication annotation");
        }

        return createApplicationContext(applicationClass);
    }

    private static boolean hasSummerApplicationAnnotation(Class<?> applicationClass) {
        return applicationClass.isAnnotationPresent(SummerApplication.class);
    }

    private static ApplicationContext createApplicationContext(Class<?> applicationClass) {
        return DefaultApplicationContext.newContext(applicationClass);
    }
}
