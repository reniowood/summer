package com.jinhyuk.summer.core.test_application.have_components;

import com.jinhyuk.summer.core.annotations.Component;
import com.jinhyuk.summer.core.annotations.SummerApplication;

@SummerApplication
public class SummerTestApplication {
    @Component
    public static class A {}
    @Component
    public static class B {}
    public static class C {}
}