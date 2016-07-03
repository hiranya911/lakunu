package org.lakunu.labs;

import junit.framework.Assert;
import org.junit.Test;

public class LabTest {

    private Lifecycle getLifecycle() {
        return Lifecycle.newBuilder(DefaultLifecycle.NAME)
                .addPlugin(DefaultLifecycle.BUILD_PHASE, new TestPlugin())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoName() {
        Lab.newBuilder().setLifecycle(getLifecycle()).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoLifecycle() {
        Lab.newBuilder().setName("foo").build();
    }

    @Test
    public void testValidLab() {
        Lab lab = Lab.newBuilder().setName("foo")
                .setLifecycle(getLifecycle())
                .build();
        Assert.assertEquals("foo", lab.getName());
    }

}
