package com.jinhyuk.summer_example;

import com.jinhuyk.summer_core.SummerMainApplication;
import com.jinhuyk.summer_core.annotations.SummerApplication;
import com.jinhuyk.summer_core.context.ApplicationContext;
import com.jinhyuk.summer_example.components.ComponentA;

@SummerApplication
public class SummerExampleApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SummerMainApplication.run(SummerExampleApplication.class);

        ComponentA componentA = applicationContext.getComponent(ComponentA.class);

        System.out.println(componentA.name());
    }
}
