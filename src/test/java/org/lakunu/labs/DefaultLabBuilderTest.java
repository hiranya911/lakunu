package org.lakunu.labs;

import org.junit.Assert;
import org.junit.Test;
import org.lakunu.labs.plugins.CustomTestPlugin;

import static org.hamcrest.CoreMatchers.is;

public class DefaultLabBuilderTest {

    @Test
    public void testDefaultLab() {
        Lab lab = DefaultLabBuilder.newBuilder()
                .setName("foo")
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, CustomTestPlugin.newInstance())
                .build();
        Assert.assertEquals("foo", lab.getName());
        Assert.assertThat(lab.getPhases(), is(DefaultLabBuilder.PHASE_ORDER));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoName() {
        DefaultLabBuilder.newBuilder()
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, CustomTestPlugin.newInstance())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPlugins() {
        DefaultLabBuilder.newBuilder()
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, CustomTestPlugin.newInstance())
                .build();
    }

    @Test
    public void testPhaseOrder() {
        Lab lab = DefaultLabBuilder.newBuilder()
                .setName("foo")
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, CustomTestPlugin.newInstance())
                .build();
        Assert.assertThat(lab.getPhases(), is(DefaultLabBuilder.PHASE_ORDER));

        lab = DefaultLabBuilder.newBuilder()
                .setName("foo")
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, CustomTestPlugin.newInstance())
                .addPlugin(DefaultLabBuilder.RUN_PHASE, CustomTestPlugin.newInstance())
                .build();
        Assert.assertThat(lab.getPhases(), is(DefaultLabBuilder.PHASE_ORDER));

        lab = DefaultLabBuilder.newBuilder()
                .setName("foo")
                .addPlugin(DefaultLabBuilder.RUN_PHASE, CustomTestPlugin.newInstance())
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, CustomTestPlugin.newInstance())
                .build();
        Assert.assertThat(lab.getPhases(), is(DefaultLabBuilder.PHASE_ORDER));
    }
}
