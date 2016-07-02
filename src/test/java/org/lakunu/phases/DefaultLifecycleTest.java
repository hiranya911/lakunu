package org.lakunu.phases;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.lakunu.labs.Lab;
import org.lakunu.labs.Lifecycle;
import org.lakunu.labs.Phase;
import org.lakunu.labs.phases.BuildPhase;
import org.lakunu.labs.phases.DefaultLifecycle;
import org.lakunu.labs.phases.RunPhase;

import java.util.List;

public class DefaultLifecycleTest {

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPhaseList() {
        DefaultLifecycle.newBuilder().build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPhase() {
        DefaultLifecycle.newBuilder().add(new BuildPhase()).add(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicatePhases() {
        DefaultLifecycle.newBuilder().add(new BuildPhase()).add(new BuildPhase()).build();
    }

    @Test
    public void testPhaseOrder() {
        Phase[] expected = { new BuildPhase(), new RunPhase() };
        Lifecycle lifecycle = DefaultLifecycle.newBuilder()
                .add(new BuildPhase()).add(new RunPhase()).build();
        List<Phase> phases = Lists.newArrayList(lifecycle.getPhases());
        Assert.assertArrayEquals(expected, phases.toArray());

        lifecycle = DefaultLifecycle.newBuilder()
                .add(new RunPhase()).add(new BuildPhase()).build();
        phases = Lists.newArrayList(lifecycle.getPhases());
        Assert.assertArrayEquals(expected, phases.toArray());
    }

}
