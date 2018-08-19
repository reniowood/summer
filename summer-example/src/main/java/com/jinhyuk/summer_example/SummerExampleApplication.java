package com.jinhyuk.summer_example;

import com.jinhuyk.summer_core.SummerMainApplication;
import com.jinhuyk.summer_core.annotations.SummerApplication;
import com.jinhyuk.summer_example.components.ComponentA;

@SummerApplication
public class SummerExampleApplication {
    public static void main(String[] args) {
        SummerMainApplication.run(SummerExampleApplication.class);

        ComponentA componentA = (ComponentA) SummerMainApplication.getComponent("ComponentA");

        System.out.println(componentA.name());
    }
}
