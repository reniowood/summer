package com.jinhyuk.summer_core.test_application.have_dependencies.components;

import com.jinhuyk.summer_core.annotations.Autowired;
import com.jinhuyk.summer_core.annotations.Component;

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
