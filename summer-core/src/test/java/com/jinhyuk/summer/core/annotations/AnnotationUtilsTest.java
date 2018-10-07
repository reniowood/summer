package com.jinhyuk.summer.core.annotations;

import com.jinhyuk.summer.core.annotations.test_annotations.BaseAnnotation;
import com.jinhyuk.summer.core.annotations.test_annotations.DefaultAnnotation;
import com.jinhyuk.summer.core.annotations.test_annotations.TargetAnnotation;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationUtilsTest {

    @TargetAnnotation
    private static class A {}

    @DefaultAnnotation
    private static class B {}

    @Test
    public void testFindingPresentAnnotation() {
        Assert.assertNotNull(AnnotationUtils.findAnnotation(A.class, TargetAnnotation.class));
    }

    @Test
    public void testFindingNotPresentAnnotation() {
        Assert.assertNull(AnnotationUtils.findAnnotation(B.class, TargetAnnotation.class));
    }

    @Test
    public void testFindingMetaAnnotatedAnnotation() {
        Assert.assertNotNull(AnnotationUtils.findAnnotation(A.class, BaseAnnotation.class));
    }
}
