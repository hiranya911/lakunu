package org.lakunu.labs;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.lakunu.labs.phases.BuildPhase;
import org.lakunu.labs.phases.RunPhase;

import java.util.List;

public class LabTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNoName() {
        Lab.newBuilder().build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPhaseList() {
        Lab.newBuilder().setName("l0").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPhases() {
        Lab.newBuilder().setName("l0")
                .addPhase(new BuildPhase()).addPhase(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicatePhases() {
        Lab.newBuilder().setName("l0")
                .addPhase(new BuildPhase()).addPhase(new BuildPhase()).build();
    }

    @Test
    public void testDefaultPhaseOrder() {
        Phase[] expected = { new BuildPhase(), new RunPhase() };
        Lab lab = Lab.newBuilder().setName("l0")
                .addPhase(new BuildPhase()).addPhase(new RunPhase()).build();
        List<Phase> phases = Lists.newArrayList(lab.getPhases());
        Assert.assertArrayEquals(expected, phases.toArray());

        lab = Lab.newBuilder().setName("l0")
                .addPhase(new RunPhase()).addPhase(new BuildPhase()).build();
        phases = Lists.newArrayList(lab.getPhases());
        Assert.assertArrayEquals(expected, phases.toArray());
    }

}
