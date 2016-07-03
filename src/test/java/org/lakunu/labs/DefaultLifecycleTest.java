package org.lakunu.labs;

import org.junit.Test;

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
                .addPlugin(DefaultLifecycle.BUILD_PHASE, new TestPlugin())
                .build();
        assertThat(lifecycle.getPhaseOrder(), is(DefaultLifecycle.PHASE_ORDER));

        lifecycle = Lifecycle.newBuilder(DefaultLifecycle.NAME)
                .addPlugin(DefaultLifecycle.BUILD_PHASE, new TestPlugin())
                .addPlugin(DefaultLifecycle.RUN_PHASE, new TestPlugin())
                .build();
        assertThat(lifecycle.getPhaseOrder(), is(DefaultLifecycle.PHASE_ORDER));

        lifecycle = Lifecycle.newBuilder(DefaultLifecycle.NAME)
                .addPlugin(DefaultLifecycle.RUN_PHASE, new TestPlugin())
                .addPlugin(DefaultLifecycle.BUILD_PHASE, new TestPlugin())
                .build();
        assertThat(lifecycle.getPhaseOrder(), is(DefaultLifecycle.PHASE_ORDER));
    }

}
