package org.lakunu.labs.plugins;

import org.lakunu.labs.Evaluation;

public final class TestPlugin extends Plugin {

    private final TestPluginFunction function;

    private TestPlugin(Builder builder) {
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

    public static TestPlugin newInstance() {
        return new Builder().build();
    }

    public static TestPlugin newInstance(TestPluginFunction function) {
        return new Builder().setFunction(function).build();
    }

    public static class Builder extends Plugin.Builder<TestPlugin,Builder> {

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
        public TestPlugin build() {
            return new TestPlugin(this);
        }
    }

    public interface TestPluginFunction {
        boolean run(Evaluation.Context context);
    }
}
