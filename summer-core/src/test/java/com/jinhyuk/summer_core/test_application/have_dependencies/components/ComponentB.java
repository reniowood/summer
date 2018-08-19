package com.jinhyuk.summer_core.test_application.have_dependencies.components;

import com.jinhuyk.summer_core.annotations.Autowired;
import com.jinhuyk.summer_core.annotations.Component;

@Component
public class ComponentB {
    @Autowired
    public ComponentD componentD;
}
