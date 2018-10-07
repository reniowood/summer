package com.jinhyuk.summer.core.test_application.have_dependencies.components;

import com.jinhyuk.summer.core.annotations.Autowired;
import com.jinhyuk.summer.core.annotations.Component;

@Component
public class ComponentA {
    public ComponentB componentB;
    public ComponentC componentC;

    @Autowired
    public ComponentA(ComponentB componentB, ComponentC componentC) {
        this.componentB = componentB;
        this.componentC = componentC;
    }

    public ComponentD componentD;
}
