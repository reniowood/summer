package com.jinhyuk.summer.core.test_application.have_duplicate_components;

import com.jinhyuk.summer.core.annotations.Component;
import com.jinhyuk.summer.core.annotations.SummerApplication;

@SummerApplication
public class SummerTestApplication {
    @Component(name = "X")
    private class A {}
    @Component(name = "X")
    private class B {}
}
