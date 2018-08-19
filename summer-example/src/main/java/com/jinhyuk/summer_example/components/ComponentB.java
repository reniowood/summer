package com.jinhyuk.summer_example.components;

import com.jinhuyk.summer_core.annotations.Autowired;
import com.jinhuyk.summer_core.annotations.Component;

@Component
public class ComponentB {
    @Autowired
    private ComponentC componentC;

    public String name() {
        return componentC.name();
    }
}
