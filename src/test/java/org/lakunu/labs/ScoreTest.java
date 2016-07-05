package org.lakunu.labs;

import com.google.common.collect.ImmutableList;
import junit.framework.Assert;
import org.junit.Test;

public class ScoreTest {

    @Test
    public void testPoints() {
        try {
            Score.newPoints("foo", -3, 5);
            Assert.fail("No error thrown");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            Score.newPoints("foo", 3, -5);
            Assert.fail("No error thrown");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            Score.newPoints("foo", 5, 4);
            Assert.fail("No error thrown");
        } catch (IllegalArgumentException ignored) {
        }

        Score s = Score.newPoints("foo", 3D, 5D);
        Assert.assertEquals("foo", s.getName());
        Assert.assertEquals(3D, s.getValue(), 1e-10);
        Assert.assertEquals(5D, s.getLimit(), 1e-10);
    }

    @Test
    public void testPenalties() {
        try {
            Score.newPenalty("foo", 3);
            Assert.fail("No error thrown");
        } catch (IllegalArgumentException ignored) {
        }

        Score s = Score.newPenalty("foo", -3D);
        Assert.assertEquals("foo", s.getName());
        Assert.assertEquals(-3D, s.getValue(), 1e-10);
        Assert.assertEquals(0D, s.getLimit(), 1e-10);
    }

    @Test
    public void testPointAddition() {
        Score s1 = Score.newPoints("foo", 3, 5);
        Score s2 = Score.newPoints("foo", 4, 5);
        Score s = s1.add("total", s2);
        Assert.assertEquals("total", s.getName());
        Assert.assertEquals(7D, s.getValue(), 1e-10);
        Assert.assertEquals(10D, s.getLimit(), 1e-10);
    }

    @Test
    public void testPenaltyAddition() {
        Score s1 = Score.newPenalty("foo", -3);
        Score s2 = Score.newPenalty("foo", -2);
        Score s = s1.add("total", s2);
        Assert.assertEquals("total", s.getName());
        Assert.assertEquals(-5D, s.getValue(), 1e-10);
        Assert.assertEquals(0D, s.getLimit(), 1e-10);
    }

    @Test
    public void testPointAndPenaltyAddition() {
        Score s1 = Score.newPoints("foo", 7, 10);
        Score s2 = Score.newPenalty("foo", -2);
        Score s = s1.add("total", s2);
        Assert.assertEquals("total", s.getName());
        Assert.assertEquals(5D, s.getValue(), 1e-10);
        Assert.assertEquals(10D, s.getLimit(), 1e-10);
    }

    @Test
    public void testTotal() {
        ImmutableList<Score> scores = ImmutableList.of(
                Score.newPoints("1", 4, 5),
                Score.newPoints("2", 5, 5),
                Score.newPenalty("3", -1),
                Score.newPoints("4", 0, 5),
                Score.newPenalty("5", 0)
        );
        Score s = Score.total(scores);
        Assert.assertEquals("total", s.getName());
        Assert.assertEquals(8D, s.getValue(), 1e-10);
        Assert.assertEquals(15D, s.getLimit(), 1e-10);
    }

}
