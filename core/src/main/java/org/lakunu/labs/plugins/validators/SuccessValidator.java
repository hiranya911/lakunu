package org.lakunu.labs.plugins.validators;

import org.lakunu.labs.Score;
import org.lakunu.labs.plugins.Plugin;

public final class SuccessValidator extends Validator {

    private SuccessValidator(Builder builder) {
        super(builder);
    }

    @Override
    public Score validate(Plugin.Context context) {
        return reportScore(context.isSuccess());
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends Validator.Builder<SuccessValidator,Builder> {

        private Builder() {
        }

        @Override
        protected Builder getThisObj() {
            return this;
        }

        @Override
        public SuccessValidator build() {
            return new SuccessValidator(this);
        }
    }
}
