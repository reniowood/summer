package com.jinhuyk.summer_core.components;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class MethodCreatableComponent<T> extends AbstractComponent<T> {
    private Injectable<?> configuration;
    private Method creator;

    public MethodCreatableComponent(Class<T> tClass, String name, Method creator, Injectable<?> configuration) {
        super(tClass, name);

        this.creator = creator;
        this.configuration = configuration;
    }

    @Override
    public boolean isCreatable() {
        return creator != null;
    }

    @Override
    protected Object createInstance() {
        List<Object> parameters = getParameters();

        try {
            return creator.invoke(configuration.getObject(), parameters.toArray());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Application doesn't have access to %s of component %s.", creator, getName()));
        } catch (InvocationTargetException e) {
            throw new RuntimeException(String.format("Application couldn't instantiate component %s with %s.", getName(), creator));
        }
    }

    @Override
    protected void findParameterDependencies(Map<String, AbstractComponent<?>> nameComponentMap) {
        for (Parameter parameter : creator.getParameters()) {
            String dependencyName = getParameterDependencyName(parameter);

            if (!nameComponentMap.containsKey(dependencyName)) {
                throw new RuntimeException(String.format("Dependent component %s of component %s is not found", dependencyName, getName()));
            }

            AbstractComponent<?> dependency = nameComponentMap.get(dependencyName);

            addParameterDependency(dependency);
            dependency.addInjectedDependency(this);
        }
    }
}
