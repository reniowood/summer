package com.jinhyuk.summer.core.context.test_application.have_mutual_dependencies.components;

import com.jinhyuk.summer.core.annotations.Autowired;
import com.jinhyuk.summer.core.annotations.Component;

@Component
public class ComponentA {
    @Autowired
    private ComponentB componentB;
}
