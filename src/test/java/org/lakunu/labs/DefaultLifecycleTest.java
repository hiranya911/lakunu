package org.lakunu.labs;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DefaultLifecycleTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNoPlugins() {
        DefaultLifecycle.newBuilder().build();
    }

    @Test
    public void testPhaseOrder() {
        Lifecycle lifecycle = DefaultLifecycle.newBuilder()
                .addPlugin(DefaultLifecycle.BUILD_PHASE, new TestPlugin())
                .build();
        assertThat(lifecycle.getPhaseOrder(), is(DefaultLifecycle.PHASE_ORDER));

        lifecycle = DefaultLifecycle.newBuilder()
                .addPlugin(DefaultLifecycle.BUILD_PHASE, new TestPlugin())
                .addPlugin(DefaultLifecycle.RUN_PHASE, new TestPlugin())
                .build();
        assertThat(lifecycle.getPhaseOrder(), is(DefaultLifecycle.PHASE_ORDER));

        lifecycle = DefaultLifecycle.newBuilder()
                .addPlugin(DefaultLifecycle.RUN_PHASE, new TestPlugin())
                .addPlugin(DefaultLifecycle.BUILD_PHASE, new TestPlugin())
                .build();
        assertThat(lifecycle.getPhaseOrder(), is(DefaultLifecycle.PHASE_ORDER));
    }

}
