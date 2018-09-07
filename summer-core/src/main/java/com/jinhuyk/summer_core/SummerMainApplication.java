package com.jinhuyk.summer_core;

import com.jinhuyk.summer_core.annotations.SummerApplication;
import com.jinhuyk.summer_core.context.ApplicationContext;
import com.jinhuyk.summer_core.context.DefaultApplicationContext;

public class SummerMainApplication {
    public static ApplicationContext run(Class applicationClass) {
        if (!hasSummerApplicationAnnotation(applicationClass)) {
            throw new RuntimeException("This summer application class doesn't have @SummerApplication annotation");
        }

        return createApplicationContext(applicationClass);
    }

    private static boolean hasSummerApplicationAnnotation(Class applicationClass) {
        return applicationClass.isAnnotationPresent(SummerApplication.class);
    }

    private static ApplicationContext createApplicationContext(Class applicationClass) {
        return DefaultApplicationContext.newContext(applicationClass);
    }
}
