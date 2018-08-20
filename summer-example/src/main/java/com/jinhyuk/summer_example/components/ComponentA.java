package com.jinhyuk.summer_example.components;

import com.jinhuyk.summer_core.annotations.Autowired;
import com.jinhuyk.summer_core.annotations.Component;

@Component
public class ComponentA {
    @Autowired
    private ComponentB componentB;

    public String name() {
        return componentB.name();
    }
}