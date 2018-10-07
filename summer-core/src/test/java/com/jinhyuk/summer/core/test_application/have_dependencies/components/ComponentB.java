package com.jinhyuk.summer.core.test_application.have_dependencies.components;

import com.jinhyuk.summer.core.annotations.Autowired;
import com.jinhyuk.summer.core.annotations.Component;

@Component
public class ComponentB {
    @Autowired
    public ComponentD componentD;
}
