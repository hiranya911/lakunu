package org.lakunu.labs;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import org.lakunu.labs.resources.Resources;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Lab {

    private final String name;
    private final EvaluationPlan evaluationPlan;
    private final Resources resources;

    private Lab(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.name), "Name is required");
        checkNotNull(builder.evaluationPlan, "EvaluationPlan is required");
        checkNotNull(builder.resources, "Resources instance is required");
        this.name = builder.name;
        this.evaluationPlan = builder.evaluationPlan;
        this.resources = builder.resources;
    }

    public String getName() {
        return name;
    }

    void evaluate(Evaluation.Context context, String finalPhase) {
        evaluationPlan.evaluate(context, finalPhase);
    }

    Resources getResources() {
        return resources;
    }

    public ImmutableList<Score> getRubric() {
        return evaluationPlan.getRubric();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private EvaluationPlan evaluationPlan;
        private Resources resources = new Resources(ImmutableSet.of());

        private Builder() {
        }

        public final Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEvaluationPlan(EvaluationPlan evaluationPlan) {
            this.evaluationPlan = evaluationPlan;
            return this;
        }

        public Builder setResources(Resources resources) {
            this.resources = resources;
            return this;
        }

        public final Lab build() {
            return new Lab(this);
        }
    }

}
