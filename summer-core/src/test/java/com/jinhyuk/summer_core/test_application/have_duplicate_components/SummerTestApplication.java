package com.jinhyuk.summer_core.test_application.have_duplicate_components;

import com.jinhuyk.summer_core.annotations.Component;
import com.jinhuyk.summer_core.annotations.SummerApplication;

@SummerApplication
public class SummerTestApplication {
    @Component(name = "X")
    private class A {}
    @Component(name = "X")
    private class B {}
}
