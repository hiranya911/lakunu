package org.lakunu.labs.ant;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.lakunu.labs.Score;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;

public class AntEvaluationPlanTest {

    @Test
    public void testSimplePlan() {
        File file = new File("src/test/resources/sample1.xml");
        AntEvaluationPlan plan = new AntEvaluationPlan(file, null);
        Assert.assertEquals(1, plan.getPhases().size());
        Assert.assertEquals("grade", plan.getPhases().get(0));
        Score rubric = Score.total(plan.getRubric());
        Assert.assertEquals(60D, rubric.getLimit(), 1e-10);
        Assert.assertEquals(0D, rubric.getValue(), 1e-10);
    }

    @Test
    public void testMultiTargetPlan() {
        File file = new File("src/test/resources/sample2.xml");
        AntEvaluationPlan plan = new AntEvaluationPlan(file, null);
        Assert.assertEquals(4, plan.getPhases().size());
        Assert.assertThat(plan.getPhases(), is(ImmutableList.of("clean", "build", "run", "test")));
        Score rubric = Score.total(plan.getRubric());
        Assert.assertEquals(60D, rubric.getLimit(), 1e-10);
        Assert.assertEquals(0D, rubric.getValue(), 1e-10);
    }

}
