package com.jinhuyk.summer_core.components;

import com.jinhuyk.summer_core.annotations.Autowired;

import java.lang.reflect.Parameter;
import java.util.Collection;

public interface ParameterDependent extends Dependent {
    Collection<Injectable<?>> getParameterDependencies();

    default String getParameterDependencyName(Parameter parameter) {
        if (parameter.isAnnotationPresent(Autowired.class)) {
            Autowired autowired = parameter.getAnnotation(Autowired.class);

            return autowired.name();
        } else {
            return parameter.getType().getSimpleName();
        }
    }
}
