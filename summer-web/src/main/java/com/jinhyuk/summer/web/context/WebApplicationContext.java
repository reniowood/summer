package com.jinhyuk.summer.web.context;

import com.jinhyuk.summer.core.context.AbstractApplicationContext;

public class WebApplicationContext extends AbstractApplicationContext {
    protected WebApplicationContext(String basePackageName) {
        super(basePackageName);
    }

    public static WebApplicationContext newContext(String basePackageName) {
        return new WebApplicationContext(basePackageName);
    }
}
