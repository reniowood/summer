package com.jinhyuk.summer.core.context;

import com.jinhyuk.summer.core.context.test_application.have_dependencies.components.ComponentA;
import com.jinhyuk.summer.core.context.test_application.have_dependencies.components.ComponentB;
import com.jinhyuk.summer.core.context.test_application.have_dependencies.components.ComponentC;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ApplicationContextTest {


    @Test(expected = RuntimeException.class)
    public void test_applicationShouldNotHaveComponentsHaveSameName() {
        DefaultApplicationContext.newContext(com.jinhyuk.summer.core.context.test_application.have_duplicate_components.SummerTestApplication.class);
    }

    @Test
    public void test_allClassesAnnotatedByComponentScannedByApplication() {
        ApplicationContext applicationContext = DefaultApplicationContext.newContext(com.jinhyuk.summer.core.context.test_application.have_components.SummerTestApplication.class);

        Map<String, Object> components = applicationContext.getComponents();

        Assert.assertTrue(components.containsKey("A"));
        Assert.assertEquals(com.jinhyuk.summer.core.context.test_application.have_components.SummerTestApplication.A.class, components.get("A").getClass());
        Assert.assertTrue(components.containsKey("B"));
        Assert.assertEquals(com.jinhyuk.summer.core.context.test_application.have_components.SummerTestApplication.B.class, components.get("B").getClass());
        Assert.assertFalse(components.containsKey("C"));
    }

    @Test
    public void test_injectDependenciesByAutowireProperty() {
        ApplicationContext applicationContext = DefaultApplicationContext.newContext(com.jinhyuk.summer.core.context.test_application.have_dependencies.SummerTestApplication.class);

        Map<String, Object> components = applicationContext.getComponents();

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
        DefaultApplicationContext.newContext(com.jinhyuk.summer.core.context.test_application.have_mutual_dependencies.SummerTestApplication.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_thereShouldNotBeDependencyCycles() {
        DefaultApplicationContext.newContext(com.jinhyuk.summer.core.context.test_application.have_dependency_cycle.SummerTestApplication.class);
    }

    @Test
    public void test_applicationCanGetComponentByClass() {
        ApplicationContext applicationContext = DefaultApplicationContext.newContext(com.jinhyuk.summer.core.context.test_application.have_dependencies.SummerTestApplication.class);

        ComponentA componentA = applicationContext.getComponent(ComponentA.class);
        Assert.assertNotNull(componentA);

        ComponentC componentC = applicationContext.getComponent(ComponentC.class);
        Assert.assertNotNull(componentC);
    }
}
