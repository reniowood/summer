package com.jinhyuk.summer.application;

import org.junit.Test;

public class SummerMainApplicationTest {

    @Test(expected = RuntimeException.class)
    public void test_applicationClassShouldHaveSummerApplicationAnnotation() {
        SummerMainApplication.run(com.jinhyuk.summer.application.test_application.without_annotation.SummerTestApplication.class);
    }
}
