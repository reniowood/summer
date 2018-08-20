package com.jinhyuk.summer_core;

import com.jinhuyk.summer_core.SummerMainApplication;
import com.jinhyuk.summer_core.test_application.have_dependencies.components.ComponentA;
import com.jinhyuk.summer_core.test_application.have_dependencies.components.ComponentB;
import com.jinhyuk.summer_core.test_application.have_dependencies.components.ComponentC;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

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

    @Test
    @SuppressWarnings("unchecked")
    public void test_injectDependenciesByAutowireProperty() throws NoSuchFieldException, IllegalAccessException {
        com.jinhyuk.summer_core.test_application.have_dependencies.SummerTestApplication.main(new String[] {});

        Field componentsField = SummerMainApplication.class.getDeclaredField("components");
        componentsField.setAccessible(true);
        Map<String, Object> components = (Map<String, Object>) componentsField.get(com.jinhyuk.summer_core.test_application.have_dependencies.SummerTestApplication.class);

        Field dependencyMapField = SummerMainApplication.class.getDeclaredField("dependencyMap");
        dependencyMapField.setAccessible(true);
        Map<String, Set<String>> dependencyMap = (Map<String, Set<String>>) dependencyMapField.get(com.jinhyuk.summer_core.test_application.have_dependencies.SummerTestApplication.class);

        Assert.assertTrue(components.containsKey("ComponentA"));
        ComponentA componentA = (ComponentA) components.get("ComponentA");
        Assert.assertEquals(components.get("ComponentB"), componentA.componentB);
        Assert.assertEquals(components.get("ComponentC"), componentA.componentC);

        Assert.assertTrue(dependencyMap.containsKey("ComponentA"));
        Assert.assertTrue(dependencyMap.get("ComponentA").contains("ComponentB"));
        Assert.assertTrue(dependencyMap.get("ComponentA").contains("ComponentC"));

        Assert.assertTrue(components.containsKey("ComponentB"));
        ComponentB componentB = (ComponentB) components.get("ComponentB");
        Assert.assertEquals(components.get("ComponentD"), componentB.componentD);

        Assert.assertTrue(dependencyMap.containsKey("ComponentB"));
        Assert.assertTrue(dependencyMap.get("ComponentB").contains("ComponentD"));

        Assert.assertTrue(components.containsKey("ComponentC"));
        ComponentC componentC = (ComponentC) components.get("ComponentC");
        Assert.assertEquals(components.get("ComponentD"), componentC.componentD);

        Assert.assertTrue(dependencyMap.containsKey("ComponentC"));
        Assert.assertTrue(dependencyMap.get("ComponentC").contains("ComponentD"));

        Assert.assertNull(componentA.componentD);
        Assert.assertFalse(dependencyMap.get("ComponentA").contains("ComponentD"));
    }

    @Test(expected = RuntimeException.class)
    public void test_thereShouldNotBeMutualDependencies() {
        com.jinhyuk.summer_core.test_application.have_mutual_dependencies.SummerTestApplication.main(new String[] {});
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
