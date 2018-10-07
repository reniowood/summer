package com.jinhyuk.summer.example.components;

import com.jinhyuk.summer.core.annotations.Autowired;
import com.jinhyuk.summer.core.annotations.Component;

@Component
public class ComponentB {
    @Autowired
    private ComponentC componentC;

    public String name() {
        return componentC.name();
    }
}
