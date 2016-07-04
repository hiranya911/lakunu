package org.lakunu.labs;

import junit.framework.Assert;
import org.junit.Test;
import org.lakunu.labs.plugins.TestPlugin;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DefaultLabBuilderTest {

    @Test
    public void testDefaultLab() {
        Lab lab = DefaultLabBuilder.newBuilder()
                .setName("foo")
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, TestPlugin.newInstance())
                .build();
        Assert.assertEquals("foo", lab.getName());
        assertThat(lab.getPhases(), is(DefaultLabBuilder.PHASE_ORDER));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoName() {
        DefaultLabBuilder.newBuilder()
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, TestPlugin.newInstance())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPlugins() {
        DefaultLabBuilder.newBuilder()
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, TestPlugin.newInstance())
                .build();
    }

    @Test
    public void testPhaseOrder() {
        Lab lab = DefaultLabBuilder.newBuilder()
                .setName("foo")
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, TestPlugin.newInstance())
                .build();
        assertThat(lab.getPhases(), is(DefaultLabBuilder.PHASE_ORDER));

        lab = DefaultLabBuilder.newBuilder()
                .setName("foo")
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, TestPlugin.newInstance())
                .addPlugin(DefaultLabBuilder.RUN_PHASE, TestPlugin.newInstance())
                .build();
        assertThat(lab.getPhases(), is(DefaultLabBuilder.PHASE_ORDER));

        lab = DefaultLabBuilder.newBuilder()
                .setName("foo")
                .addPlugin(DefaultLabBuilder.RUN_PHASE, TestPlugin.newInstance())
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, TestPlugin.newInstance())
                .build();
        assertThat(lab.getPhases(), is(DefaultLabBuilder.PHASE_ORDER));
    }
}
