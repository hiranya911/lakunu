package org.lakunu.labs.plugins;

import org.lakunu.labs.LabContext;

public final class TestPlugin extends Plugin {

    private TestPlugin(Builder builder) {
        super(builder);
    }

    @Override
    protected boolean doExecute(LabContext context) {
        return false;
    }

    public static TestPlugin newInstance() {
        return new Builder().build();
    }

    public static class Builder extends Plugin.Builder<TestPlugin,Builder> {

        private Builder() {
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
}
