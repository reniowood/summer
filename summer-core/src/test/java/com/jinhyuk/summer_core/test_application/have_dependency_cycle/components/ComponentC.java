package com.jinhyuk.summer_core.test_application.have_dependency_cycle.components;

import com.jinhuyk.summer_core.annotations.Autowired;
import com.jinhuyk.summer_core.annotations.Component;

@Component
public class ComponentC {
    @Autowired
    private ComponentD componentD;
}
