package org.lakunu.labs;

import junit.framework.Assert;
import org.junit.Test;
import org.lakunu.labs.plugins.TestPlugin;

public class LabTest {

    private Lifecycle getLifecycle() {
        return Lifecycle.newBuilder(DefaultLifecycle.NAME)
                .addPlugin(DefaultLifecycle.BUILD_PHASE, TestPlugin.newInstance())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoName() {
        Lab.newBuilder().setLifecycle(getLifecycle()).build();
    }

    @Test(expected = NullPointerException.class)
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
