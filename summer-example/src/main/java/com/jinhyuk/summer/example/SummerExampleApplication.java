package com.jinhyuk.summer.example;

import com.jinhyuk.summer.core.SummerMainApplication;
import com.jinhyuk.summer.core.annotations.SummerApplication;
import com.jinhyuk.summer.core.context.ApplicationContext;
import com.jinhyuk.summer.example.components.ComponentA;

@SummerApplication
public class SummerExampleApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SummerMainApplication.run(SummerExampleApplication.class);

        ComponentA componentA = applicationContext.getComponent(ComponentA.class);

        System.out.println(componentA.name());
    }
}
