package com.jinhyuk.summer.web.context.test_application;

import com.jinhyuk.summer.core.annotations.Autowired;
import com.jinhyuk.summer.web.annotations.Controller;

@Controller
public class ControllerA {

    @Autowired
    private ComponentA componentA;
}
