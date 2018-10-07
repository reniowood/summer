package com.jinhyuk.summer.example.components;

import com.jinhyuk.summer.core.annotations.Autowired;
import com.jinhyuk.summer.core.annotations.Component;

@Component
public class ComponentA {
    @Autowired
    private ComponentB componentB;

    public String name() {
        return componentB.name();
    }
}
