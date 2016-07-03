package org.lakunu.labs;

import org.junit.Test;
import org.lakunu.labs.plugins.TestPlugin;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DefaultLifecycleTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNoPlugins() {
        Lifecycle.newBuilder(DefaultLifecycle.NAME).build();
    }

    @Test
    public void testPhaseOrder() {
        Lifecycle lifecycle = Lifecycle.newBuilder(DefaultLifecycle.NAME)
                .addPlugin(DefaultLifecycle.BUILD_PHASE, TestPlugin.newInstance())
                .build();
        assertThat(lifecycle.getPhaseOrder(), is(DefaultLifecycle.PHASE_ORDER));

        lifecycle = Lifecycle.newBuilder(DefaultLifecycle.NAME)
                .addPlugin(DefaultLifecycle.BUILD_PHASE, TestPlugin.newInstance())
                .addPlugin(DefaultLifecycle.RUN_PHASE, TestPlugin.newInstance())
                .build();
        assertThat(lifecycle.getPhaseOrder(), is(DefaultLifecycle.PHASE_ORDER));

        lifecycle = Lifecycle.newBuilder(DefaultLifecycle.NAME)
                .addPlugin(DefaultLifecycle.RUN_PHASE, TestPlugin.newInstance())
                .addPlugin(DefaultLifecycle.BUILD_PHASE, TestPlugin.newInstance())
                .build();
        assertThat(lifecycle.getPhaseOrder(), is(DefaultLifecycle.PHASE_ORDER));
    }

}
