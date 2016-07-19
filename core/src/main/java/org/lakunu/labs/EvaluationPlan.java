package org.lakunu.labs;

import com.google.common.collect.ImmutableList;

public interface EvaluationPlan {

    void evaluate(Evaluation.Context context, String finalPhase);

    ImmutableList<String> getPhases();

    ImmutableList<Score> getRubric();

}
