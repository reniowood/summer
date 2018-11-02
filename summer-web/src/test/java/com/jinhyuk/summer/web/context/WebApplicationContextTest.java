package com.jinhyuk.summer.web.context;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class WebApplicationContextTest {

    @Test
    public void test_webApplicationContextShouldScanAllControllerComponents() {
        WebApplicationContext webApplicationContext = WebApplicationContext.newContext("com.jinhyuk.summer.web.context.test_application");

        Map<String, Object> components = webApplicationContext.getComponents();

        Assert.assertTrue(components.containsKey("ControllerA"));
        Assert.assertTrue(components.containsKey("ControllerB"));
    }
}
