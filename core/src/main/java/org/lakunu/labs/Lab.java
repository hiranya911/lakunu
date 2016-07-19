package org.lakunu.labs;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import org.lakunu.labs.resources.Resource;
import org.lakunu.labs.resources.ResourceCollection;
import org.lakunu.labs.resources.Resources;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Lab {

    private final String name;
    private final EvaluationPlan evaluationPlan;
    private final Resources resources;

    private Lab(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.name), "Name is required");
        checkNotNull(builder.evaluationPlan, "EvaluationPlan is required");
        this.name = builder.name;
        this.evaluationPlan = builder.evaluationPlan;
        this.resources = builder.resources.build();
    }

    public String getName() {
        return name;
    }

    File prepareResources(Evaluation.Context context) throws IOException {
        return resources.prepare(context);
    }

    void evaluate(Evaluation.Context context, String finalPhase) {
        evaluationPlan.evaluate(context, finalPhase);
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
        private final Resources.Builder resources = Resources.newBuilder();

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

        public final Builder addResource(Resource resource) {
            this.resources.addResource(resource);
            return this;
        }

        public final Builder setCollection(ResourceCollection collection) {
            this.resources.setCollection(collection);
            return this;
        }

        public final Lab build() {
            return new Lab(this);
        }
    }

}
