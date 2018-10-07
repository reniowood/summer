package com.jinhyuk.summer.core.components;

import com.google.common.collect.ImmutableList;
import com.jinhyuk.summer.core.annotations.Autowired;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractComponent<T> implements Injectable<T>, Creatable<T>, FieldDependent, ParameterDependent {
    private Collection<Injectable<?>> fieldDependencies, parameterDependencies;
    private Collection<Dependent> injectedDependencies;
    private Class<T> tClass;
    private String name;
    private T object;

    AbstractComponent(Class<T> tClass, String name) {
        this.fieldDependencies = new ArrayList<>();
        this.parameterDependencies = new ArrayList<>();
        this.injectedDependencies = new ArrayList<>();
        this.tClass = tClass;
        this.name = name;
    }

    public boolean hasNoDependency() {
        return fieldDependencies.isEmpty() && parameterDependencies.isEmpty();
    }

    public int getNumDependencies() {
        return fieldDependencies.size() + parameterDependencies.size();
    }

    public Collection<Injectable<?>> getDependencies() {
        return ImmutableList.<Injectable<?>>builder().addAll(fieldDependencies).addAll(parameterDependencies).build();
    }

    @Override
    public Collection<Injectable<?>> getFieldDependencies() {
        return ImmutableList.copyOf(fieldDependencies);
    }

    protected void addFieldDependency(Injectable<?> dependency) {
        this.fieldDependencies.add(dependency);
    }

    @Override
    public Collection<Injectable<?>> getParameterDependencies() {
        return ImmutableList.copyOf(parameterDependencies);
    }

    protected void addParameterDependency(Injectable<?> dependency) {
        this.parameterDependencies.add(dependency);
    }

    @Override
    public Collection<Dependent> getInjectedDependencies() {
        return ImmutableList.copyOf(injectedDependencies);
    }

    protected void addInjectedDependency(Dependent component) {
        injectedDependencies.add(component);
    }

    @Override
    public Class<T> getBaseClass() {
        return tClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T create() {
        object = (T) createInstance();

        return object;
    }

    @Override
    public T getObject() {
        return object;
    }

    protected abstract Object createInstance();

    public void findDependencies(Map<String, AbstractComponent<?>> nameComponentMap) {
        findFieldDependencies(nameComponentMap);
        findParameterDependencies(nameComponentMap);
    }

    private void findFieldDependencies(Map<String, AbstractComponent<?>> nameComponentMap) {
        for (Field field : getBaseClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Autowired autowired = field.getAnnotation(Autowired.class);

                String dependencyName = autowired.name().isEmpty() ? field.getType().getSimpleName() : autowired.name();

                if (!nameComponentMap.containsKey(dependencyName)) {
                    throw new RuntimeException(String.format("Dependent component %s of component %s is not found", dependencyName, getName()));
                }

                AbstractComponent<?> dependency = nameComponentMap.get(dependencyName);

                addFieldDependency(dependency);
                dependency.addInjectedDependency(this);
            }
        }
    }

    protected abstract void findParameterDependencies(Map<String, AbstractComponent<?>> nameComponentMap);

    List<Object> getParameters() {
        Collection<Injectable<?>> parameterDependencies = getParameterDependencies();
        List<Object> parameters = new ArrayList<>();

        for (Injectable<?> dependency : parameterDependencies) {
            String parameterComponentName = dependency.getName();

            if (dependency.getObject() != null) {
                parameters.add(dependency.getObject());
            } else {
                throw new RuntimeException(String.format("AbstractComponent %s has unresolved parameter %s.", getName(), parameterComponentName));
            }
        }

        return parameters;
    }
}
