package com.jinhyuk.summer.core.annotations;

import java.lang.annotation.Annotation;
import java.util.HashSet;

public class AnnotationUtils {
    public static <T extends Annotation> T findAnnotation(Class<?> aClass, Class<T> annotationClass) {
        return findAnnotation(aClass, annotationClass, new HashSet<>());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T findAnnotation(Class<?> aClass, Class<T> annotationClass, HashSet<Annotation> visitedSet) {
        for (Annotation annotation : aClass.getAnnotations()) {
            if (!visitedSet.contains(annotation)) {
                if (annotation.annotationType().equals(annotationClass)) {
                    return (T) annotation;
                }

                visitedSet.add(annotation);

                T foundAnnotation = findAnnotation(annotation.annotationType(), annotationClass, visitedSet);
                if (foundAnnotation != null) {
                    return foundAnnotation;
                }
            }
        }

        return null;
    }
}
