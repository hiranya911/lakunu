package org.lakunu.labs;

import com.google.common.collect.ImmutableList;

public class TestEvaluationPlan implements EvaluationPlan {

    private final ImmutableList<Step> steps;

    public TestEvaluationPlan(ImmutableList<Step> steps) {
        this.steps = steps;
    }

    @Override
    public void evaluate(Evaluation.Context context, String finalPhase) {
        steps.forEach(s -> s.run(context));
    }

    @Override
    public ImmutableList<String> getPhases() {
        return null;
    }

    @Override
    public ImmutableList<Score> getRubric() {
        return null;
    }

    public interface Step {
        void run(Evaluation.Context context);
    }
}
