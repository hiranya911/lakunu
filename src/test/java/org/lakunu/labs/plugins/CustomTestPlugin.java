package org.lakunu.labs.plugins;

import org.lakunu.labs.Evaluation;

public final class CustomTestPlugin extends Plugin {

    private final TestPluginFunction function;

    private CustomTestPlugin(Builder builder) {
        super(builder);
        this.function = builder.function;
    }

    @Override
    protected boolean doExecute(Evaluation.Context context) {
        if (function != null) {
            return function.run(context);
        }
        return false;
    }

    public static CustomTestPlugin newInstance() {
        return new Builder().build();
    }

    public static CustomTestPlugin newInstance(TestPluginFunction function) {
        return new Builder().setFunction(function).build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends Plugin.Builder<CustomTestPlugin,Builder> {

        private TestPluginFunction function;

        private Builder() {
        }

        public Builder setFunction(TestPluginFunction function) {
            this.function = function;
            return this;
        }

        @Override
        protected Builder getThisObj() {
            return this;
        }

        @Override
        public CustomTestPlugin build() {
            return new CustomTestPlugin(this);
        }
    }

    public interface TestPluginFunction {
        boolean run(Evaluation.Context context);
    }
}
