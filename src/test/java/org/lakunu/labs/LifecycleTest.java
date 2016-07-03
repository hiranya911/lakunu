package org.lakunu.labs;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.lakunu.labs.plugins.TestPlugin;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class LifecycleTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullPhases() {
        new TestLifecycle(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPhases() {
        new TestLifecycle(ImmutableList.of()).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPlugins() {
        new TestLifecycle(ImmutableList.of("foo")).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPhaseName1() {
        new TestLifecycle(ImmutableList.of("foo", "_bar"))
                .addPlugin("foo", TestPlugin.newInstance())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPhaseName2() {
        new TestLifecycle(ImmutableList.of("foo", "bar_1"))
                .addPlugin("foo", TestPlugin.newInstance())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPhaseName3() {
        new TestLifecycle(ImmutableList.of("foo", "b@r"))
                .addPlugin("foo", TestPlugin.newInstance())
                .build();
    }

    @Test
    public void testValidPhaseNames() {
        ImmutableList<String> phases = ImmutableList.of("foo", "bar", "more-complex");
        Lifecycle lifecycle = new TestLifecycle(phases)
                .addPlugin("foo", TestPlugin.newInstance())
                .build();
        Assert.assertThat(phases, is(lifecycle.getPhaseOrder()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicatePhaseNames() {
        ImmutableList<String> phases = ImmutableList.of("foo", "bar", "foo");
        Lifecycle lifecycle = new TestLifecycle(phases)
                .addPlugin("foo", TestPlugin.newInstance())
                .build();
        Assert.assertThat(phases, is(lifecycle.getPhaseOrder()));
    }

    static class TestLifecycle extends Lifecycle.Builder {
        private TestLifecycle(List<String> phases) {
            super(phases);
        }
    }

}
