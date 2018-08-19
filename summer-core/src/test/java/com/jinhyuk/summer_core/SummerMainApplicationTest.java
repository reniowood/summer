package com.jinhyuk.summer_core;

import com.jinhuyk.summer_core.SummerMainApplication;
import org.junit.Test;

public class SummerMainApplicationTest {
    private static class SummerTestApplication {
        public static void main(String[] args) {
            SummerMainApplication.run(SummerTestApplication.class);
        }
    }

    @Test(expected = RuntimeException.class)
    public void test_applicationClassShouldHaveSummerApplicationAnnotation() {
        SummerTestApplication.main(new String[] {});
    }
}
