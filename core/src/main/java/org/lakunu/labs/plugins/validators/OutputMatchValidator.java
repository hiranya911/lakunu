package org.lakunu.labs.plugins.validators;

import com.google.common.base.Strings;
import org.lakunu.labs.Score;
import org.lakunu.labs.plugins.Plugin;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

public final class OutputMatchValidator extends Validator {

    private final Pattern pattern;
    private final boolean error;

    private OutputMatchValidator(Builder builder) {
        super(builder);
        checkArgument(!Strings.isNullOrEmpty(builder.pattern), "Pattern is required");
        this.pattern = Pattern.compile(builder.pattern);
        this.error = builder.error;
    }

    @Override
    public Score validate(Plugin.Context context) {
        String output;
        if (error) {
            output = context.getErrors();
        } else {
            output = context.getOutput();
        }
        return reportScore(output != null && pattern.matcher(output).matches());
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends Validator.Builder<OutputMatchValidator,Builder> {

        private String pattern;
        private boolean error;

        private Builder() {
        }

        public Builder setPattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder setError(boolean error) {
            this.error = error;
            return this;
        }

        @Override
        protected Builder getThisObj() {
            return this;
        }

        @Override
        public OutputMatchValidator build() {
            return new OutputMatchValidator(this);
        }
    }
}
