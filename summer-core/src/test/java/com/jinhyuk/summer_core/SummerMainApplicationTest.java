package com.jinhyuk.summer_core;

import com.jinhuyk.summer_core.SummerMainApplication;
import com.jinhyuk.summer_core.test_application.have_dependencies.components.ComponentA;
import com.jinhyuk.summer_core.test_application.have_dependencies.components.ComponentB;
import com.jinhyuk.summer_core.test_application.have_dependencies.components.ComponentC;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

public class SummerMainApplicationTest {
    @Before
    public void setUp() {
        SummerMainApplication.init();
    }

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

    @Test
    @SuppressWarnings("unchecked")
    public void test_injectDependenciesByAutowireProperty() throws NoSuchFieldException, IllegalAccessException {
        com.jinhyuk.summer_core.test_application.have_dependencies.SummerTestApplication.main(new String[] {});

        Field componentsField = SummerMainApplication.class.getDeclaredField("components");
        componentsField.setAccessible(true);
        Map<String, Object> components = (Map<String, Object>) componentsField.get(com.jinhyuk.summer_core.test_application.have_dependencies.SummerTestApplication.class);

        Assert.assertTrue(components.containsKey("ComponentA"));
        ComponentA componentA = (ComponentA) components.get("ComponentA");
        Assert.assertEquals(components.get("ComponentB"), componentA.componentB);
        Assert.assertEquals(components.get("ComponentC"), componentA.componentC);

        Assert.assertTrue(components.containsKey("ComponentB"));
        ComponentB componentB = (ComponentB) components.get("ComponentB");
        Assert.assertEquals(components.get("ComponentD"), componentB.componentD);

        Assert.assertTrue(components.containsKey("ComponentC"));
        ComponentC componentC = (ComponentC) components.get("ComponentC");
        Assert.assertEquals(components.get("ComponentD"), componentC.componentD);

        Assert.assertNull(componentA.componentD);
    }

    @Test(expected = RuntimeException.class)
    public void test_thereShouldNotBeMutualDependencies() {
        com.jinhyuk.summer_core.test_application.have_mutual_dependencies.SummerTestApplication.main(new String[] {});
    }

    @Test(expected = RuntimeException.class)
    public void test_thereShouldNotBeDependencyCycles() {
        com.jinhyuk.summer_core.test_application.have_dependency_cycle.SummerTestApplication.main(new String[] {});
    }

    @Test
    public void test_applicationCanGetComponentByClass() {
        com.jinhyuk.summer_core.test_application.have_dependencies.SummerTestApplication.main(new String[] {});

        ComponentA componentA = SummerMainApplication.getComponent(ComponentA.class);
        Assert.assertNotNull(componentA);

        ComponentC componentC = SummerMainApplication.getComponent(ComponentC.class);
        Assert.assertNotNull(componentC);
    }
}
