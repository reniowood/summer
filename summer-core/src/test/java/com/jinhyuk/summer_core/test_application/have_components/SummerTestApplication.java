package com.jinhyuk.summer_core.test_application.have_components;

import com.jinhuyk.summer_core.SummerMainApplication;
import com.jinhuyk.summer_core.annotations.Component;
import com.jinhuyk.summer_core.annotations.SummerApplication;

@SummerApplication
public class SummerTestApplication {
    @Component
    public static class A {}
    @Component
    public static class B {}
    public static class C {}

    public static void main(String[] args) {
        SummerMainApplication.run(SummerTestApplication.class);
    }
}