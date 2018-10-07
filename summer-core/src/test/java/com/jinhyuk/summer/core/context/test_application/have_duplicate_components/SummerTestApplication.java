package com.jinhyuk.summer.core.context.test_application.have_duplicate_components;

import com.jinhyuk.summer.core.annotations.Component;

public class SummerTestApplication {
    @Component(name = "X")
    private class A {}
    @Component(name = "X")
    private class B {}
}
