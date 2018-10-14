package com.jinhyuk.summer.core.context;

public class DefaultApplicationContext extends AbstractApplicationContext {
    private DefaultApplicationContext(Class<?> applicationClass) {
        super(applicationClass.getPackage().getName());
    }

    private DefaultApplicationContext(String basePackageName) {
        super(basePackageName);
    }

    public static ApplicationContext newContext(Class<?> applicationClass) {
        return new DefaultApplicationContext(applicationClass);
    }

    public static ApplicationContext newContext(String basePackageName) {
        return new DefaultApplicationContext(basePackageName);
    }
}
