package com.jinhyuk.summer_core;

import com.jinhuyk.summer_core.SummerMainApplication;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

public class SummerMainApplicationTest {
    @Test(expected = RuntimeException.class)
    public void test_applicationClassShouldHaveSummerApplicationAnnotation() {
        com.jinhyuk.summer_core.test_application.without_annotation.SummerTestApplication.main(new String[] {});
    }

    @Test(expected = RuntimeException.class)
    public void test_applicationShouldNotHaveComponentsHaveSameName() {
        com.jinhyuk.summer_core.test_application.have_duplicate_components.SummerTestApplication.main(new String[] {});
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_allClassesAnnotatedByComponentScannedByApplication() throws NoSuchFieldException, IllegalAccessException {
        com.jinhyuk.summer_core.test_application.have_components.SummerTestApplication.main(new String[] {});

        Field componentsField = SummerMainApplication.class.getDeclaredField("components");
        componentsField.setAccessible(true);
        Map<String, Object> components = (Map<String, Object>) componentsField.get(com.jinhyuk.summer_core.test_application.have_components.SummerTestApplication.class);

        Assert.assertTrue(components.containsKey("A"));
        Assert.assertEquals(com.jinhyuk.summer_core.test_application.have_components.SummerTestApplication.A.class, components.get("A").getClass());
        Assert.assertTrue(components.containsKey("B"));
        Assert.assertEquals(com.jinhyuk.summer_core.test_application.have_components.SummerTestApplication.B.class, components.get("B").getClass());
        Assert.assertFalse(components.containsKey("C"));
    }
}
