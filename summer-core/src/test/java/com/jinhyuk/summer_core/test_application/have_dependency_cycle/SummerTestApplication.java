package com.jinhyuk.summer_core.test_application.have_dependency_cycle;

import com.jinhuyk.summer_core.SummerMainApplication;
import com.jinhuyk.summer_core.annotations.SummerApplication;

@SummerApplication
public class SummerTestApplication {
    public static void main(String[] args) {
        SummerMainApplication.run(SummerMainApplication.class);
    }
}
