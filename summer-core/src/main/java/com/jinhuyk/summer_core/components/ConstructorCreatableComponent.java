package com.jinhuyk.summer_core.components;

import com.jinhuyk.summer_core.annotations.Autowired;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class ConstructorCreatableComponent<T> extends AbstractComponent<T> {
    private Constructor<T> constructor;

    public ConstructorCreatableComponent(Class<T> tClass, String name) {
        super(tClass, name);

        this.constructor = findCreatableConstructor();
    }

    @SuppressWarnings("unchecked")
    private Constructor<T> findCreatableConstructor() {
        Constructor<T> creatableConstructor = null;

        Constructor<?>[] constructors = getBaseClass().getConstructors();
        if (constructors.length == 1) {
            return (Constructor<T>) constructors[0];
        }

        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                if (creatableConstructor != null) {
                    throw new RuntimeException(String.format("There are more than one constructors have Autowired annotation in Component %s", getName()));
                }

                creatableConstructor = (Constructor<T>) constructor;
            }
        }

        if (creatableConstructor == null) {
            throw new RuntimeException(String.format("There is no autowired constructor or default constructor in Component %s", getName()));
        }

        return creatableConstructor;
    }

    @Override
    public boolean isCreatable() {
        return constructor != null;
    }

    @Override
    protected Object createInstance() {
        List<Object> parameters = getParameters();

        try {
            return constructor.newInstance(parameters.toArray());
        } catch (InstantiationException e) {
            throw new RuntimeException(String.format("Component %s cannot be instantiated with %s.", getName(), constructor));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Application doesn't have access to %s of component %s.", constructor, getName()));
        } catch (InvocationTargetException e) {
            throw new RuntimeException(String.format("Application couldn't instantiate component %s with %s.", getName(), constructor));
        }
    }

    @Override
    public void findParameterDependencies(Map<String, AbstractComponent<?>> nameComponentMap) {
        for (Parameter parameter : constructor.getParameters()) {
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
