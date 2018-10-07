package com.jinhyuk.summer.core.context;

import java.util.Map;

public interface ApplicationContext {
    Object getComponent(String name);
    <T> T getComponent(Class<T> aClass);

    Map<String, Object> getComponents();
}
