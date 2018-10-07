package com.jinhyuk.summer.core.components;

import java.util.Collection;

public interface Injectable<T> {
    Class<T> getBaseClass();
    String getName();
    T getObject();
    Collection<Dependent> getInjectedDependencies();
}
