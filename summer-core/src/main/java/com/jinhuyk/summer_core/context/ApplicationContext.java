package com.jinhuyk.summer_core.context;

public interface ApplicationContext {
    Object getComponent(String name);
    <T> T getComponent(Class<T> aClass);
}
