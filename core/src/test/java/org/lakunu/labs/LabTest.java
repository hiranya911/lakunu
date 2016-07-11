package org.lakunu.labs;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.lakunu.labs.plugins.CustomTestPlugin;

import static org.hamcrest.CoreMatchers.is;

public class LabTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullPhases() {
        new TestLabBuilder(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPhases() {
        new TestLabBuilder(ImmutableList.of()).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPhaseName1() {
        new TestLabBuilder(ImmutableList.of("foo", "_bar"))
                .addPlugin("foo", CustomTestPlugin.newInstance())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPhaseName2() {
        new TestLabBuilder(ImmutableList.of("foo", "bar_1"))
                .addPlugin("foo", CustomTestPlugin.newInstance())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPhaseName3() {
        new TestLabBuilder(ImmutableList.of("foo", "b@r"))
                .addPlugin("foo", CustomTestPlugin.newInstance())
                .build();
    }

    @Test
    public void testValidPhaseNames() {
        ImmutableList<String> phases = ImmutableList.of("foo", "bar", "more-complex");
        Lab lab = new TestLabBuilder(phases)
                .setName("lab")
                .addPlugin("foo", CustomTestPlugin.newInstance())
                .build();
        Assert.assertThat(phases, is(lab.getPhases()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicatePhaseNames() {
        ImmutableList<String> phases = ImmutableList.of("foo", "bar", "foo");
        Lab lab = new TestLabBuilder(phases)
                .setName("lab")
                .addPlugin("foo", CustomTestPlugin.newInstance())
                .build();
        Assert.assertThat(phases, is(lab.getPhases()));
    }

    static class TestLabBuilder extends Lab.Builder {

        private ImmutableList<String> phases;

        private TestLabBuilder(ImmutableList<String> phases) {
            this.phases = phases;
        }

        @Override
        protected ImmutableList<String> getPhases() {
            return phases;
        }
    }

}
