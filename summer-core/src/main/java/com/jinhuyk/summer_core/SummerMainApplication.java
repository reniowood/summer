package com.jinhuyk.summer_core;

import com.jinhuyk.summer_core.annotations.SummerApplication;

import java.lang.annotation.Annotation;

public class SummerMainApplication {
    public static void run(Class applicationClass) {
        if (!hasSummerApplicationAnnotation(applicationClass)) {
            throw new RuntimeException("This summer application class doesn't have @SummerApplication annotation");
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
}
