package org.lakunu.labs.plugins.validators;

import junit.framework.Assert;
import org.junit.Test;
import org.lakunu.labs.Score;
import org.lakunu.labs.plugins.Plugin;

import java.util.concurrent.atomic.AtomicBoolean;

public class ValidatorTest {

    @Test
    public void testPositiveFullScoring() {
        AtomicBoolean condition = new AtomicBoolean(true);
        Validator val = new Validator("test", 5D) {
            @Override
            public Score validate(Plugin.Context context) {
                return reportScore(condition.get());
            }
        };
        Score score = val.validate(null);
        Assert.assertEquals("test", score.getName());
        Assert.assertEquals(5D, score.getLimit(), 1e-10);
        Assert.assertEquals(5D, score.getValue(), 1e-10);

        condition.set(false);
        score = val.validate(null);
        Assert.assertEquals("test", score.getName());
        Assert.assertEquals(5D, score.getLimit(), 1e-10);
        Assert.assertEquals(0D, score.getValue(), 1e-10);
    }

    @Test
    public void testNegativeFullScoring() {
        AtomicBoolean condition = new AtomicBoolean(true);
        Validator val = new Validator("test", -5D) {
            @Override
            public Score validate(Plugin.Context context) {
                return reportScore(condition.get());
            }
        };
        Score score = val.validate(null);
        Assert.assertEquals("test", score.getName());
        Assert.assertEquals(0D, score.getLimit(), 1e-10);
        Assert.assertEquals(-5D, score.getValue(), 1e-10);

        condition.set(false);
        score = val.validate(null);
        Assert.assertEquals("test", score.getName());
        Assert.assertEquals(0D, score.getLimit(), 1e-10);
        Assert.assertEquals(0D, score.getValue(), 1e-10);
    }

    @Test
    public void testPositivePartialScoring() {
        Validator val = new Validator("test", 5D) {
            @Override
            public Score validate(Plugin.Context context) {
                return reportScore(1D);
            }
        };
        Score score = val.validate(null);
        Assert.assertEquals("test", score.getName());
        Assert.assertEquals(5D, score.getLimit(), 1e-10);
        Assert.assertEquals(1D, score.getValue(), 1e-10);
    }

    @Test
    public void testPositivePartialScoringWithLimit() {
        Validator val = new Validator("test", 5D) {
            @Override
            public Score validate(Plugin.Context context) {
                return reportScoreWithLimit(7D);
            }
        };
        Score score = val.validate(null);
        Assert.assertEquals("test", score.getName());
        Assert.assertEquals(5D, score.getLimit(), 1e-10);
        Assert.assertEquals(5D, score.getValue(), 1e-10);
    }

    @Test
    public void testNegativePartialScoringWithLimit() {
        Validator val = new Validator("test", -5D) {
            @Override
            public Score validate(Plugin.Context context) {
                return reportScoreWithLimit(-7D);
            }
        };
        Score score = val.validate(null);
        Assert.assertEquals("test", score.getName());
        Assert.assertEquals(0D, score.getLimit(), 1e-10);
        Assert.assertEquals(-5D, score.getValue(), 1e-10);
    }

    @Test
    public void testNegativePartialScoring() {
        Validator val = new Validator("test", -5D) {
            @Override
            public Score validate(Plugin.Context context) {
                return reportScore(-2);
            }
        };
        Score score = val.validate(null);
        Assert.assertEquals("test", score.getName());
        Assert.assertEquals(0D, score.getLimit(), 1e-10);
        Assert.assertEquals(-2D, score.getValue(), 1e-10);
    }

}
