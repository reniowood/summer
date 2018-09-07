package com.jinhyuk.summer_core;

import com.jinhuyk.summer_core.SummerMainApplication;
import com.jinhuyk.summer_core.context.ApplicationContext;
import com.jinhyuk.summer_core.test_application.have_dependencies.components.ComponentA;
import com.jinhyuk.summer_core.test_application.have_dependencies.components.ComponentB;
import com.jinhyuk.summer_core.test_application.have_dependencies.components.ComponentC;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

public class SummerMainApplicationTest {

    @Test(expected = RuntimeException.class)
    public void test_applicationClassShouldHaveSummerApplicationAnnotation() {
        SummerMainApplication.run(com.jinhyuk.summer_core.test_application.without_annotation.SummerTestApplication.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_applicationShouldNotHaveComponentsHaveSameName() {
        SummerMainApplication.run(com.jinhyuk.summer_core.test_application.have_duplicate_components.SummerTestApplication.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_allClassesAnnotatedByComponentScannedByApplication() throws NoSuchFieldException, IllegalAccessException {
        ApplicationContext applicationContext = SummerMainApplication.run(com.jinhyuk.summer_core.test_application.have_components.SummerTestApplication.class);

        Field componentsField = applicationContext.getClass().getDeclaredField("components");
        componentsField.setAccessible(true);
        Map<String, Object> components = (Map<String, Object>) componentsField.get(applicationContext);

        Assert.assertTrue(components.containsKey("A"));
        Assert.assertEquals(com.jinhyuk.summer_core.test_application.have_components.SummerTestApplication.A.class, components.get("A").getClass());
        Assert.assertTrue(components.containsKey("B"));
        Assert.assertEquals(com.jinhyuk.summer_core.test_application.have_components.SummerTestApplication.B.class, components.get("B").getClass());
        Assert.assertFalse(components.containsKey("C"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_injectDependenciesByAutowireProperty() throws NoSuchFieldException, IllegalAccessException {
        ApplicationContext applicationContext = SummerMainApplication.run(com.jinhyuk.summer_core.test_application.have_dependencies.SummerTestApplication.class);

        Field componentsField = applicationContext.getClass().getDeclaredField("components");
        componentsField.setAccessible(true);
        Map<String, Object> components = (Map<String, Object>) componentsField.get(applicationContext);

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
        SummerMainApplication.run(com.jinhyuk.summer_core.test_application.have_mutual_dependencies.SummerTestApplication.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_thereShouldNotBeDependencyCycles() {
        SummerMainApplication.run(com.jinhyuk.summer_core.test_application.have_dependency_cycle.SummerTestApplication.class);
    }

    @Test
    public void test_applicationCanGetComponentByClass() {
        ApplicationContext applicationContext = SummerMainApplication.run(com.jinhyuk.summer_core.test_application.have_dependencies.SummerTestApplication.class);

        ComponentA componentA = applicationContext.getComponent(ComponentA.class);
        Assert.assertNotNull(componentA);

        ComponentC componentC = applicationContext.getComponent(ComponentC.class);
        Assert.assertNotNull(componentC);
    }
}
