package org.lakunu.labs.plugins;

import com.google.common.base.Strings;
import org.lakunu.labs.LabContext;
import org.lakunu.labs.Plugin;
import org.lakunu.labs.utils.SystemCommand;

import static com.google.common.base.Preconditions.checkArgument;

public final class AntPlugin extends Plugin {

    private final String antBinary;
    private final String buildTarget;

    private AntPlugin(Builder builder) {
        super(builder);
        checkArgument(!Strings.isNullOrEmpty(builder.antBinary), "Ant binary is required");
        checkArgument(!Strings.isNullOrEmpty(builder.buildTarget), "Ant build target is required");
        this.antBinary = builder.antBinary;
        this.buildTarget = builder.buildTarget;
    }

    @Override
    protected boolean doExecute(LabContext context) throws Exception {
        SystemCommand command = SystemCommand.newBuilder()
                .setCommand(antBinary)
                .addArgument(buildTarget)
                .setWorkingDir(context.getWorkingDir())
                .setOutputHandler(context.getOutputHandler())
                .build();
        return command.run() == 0;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends Plugin.Builder<AntPlugin,Builder> {
        private String antBinary;
        private String buildTarget;

        private Builder() {
        }

        public Builder setAntBinary(String antBinary) {
            this.antBinary = antBinary;
            return this;
        }

        public Builder setBuildTarget(String buildTarget) {
            this.buildTarget = buildTarget;
            return this;
        }

        @Override
        protected Builder getThisObj() {
            return this;
        }

        @Override
        public AntPlugin build() {
            return new AntPlugin(this);
        }
    }
}
