package org.lakunu.labs.plugins;

import org.lakunu.labs.plugins.utils.SystemCommand;

public final class AntPlugin extends Plugin {

    private final SystemCommand command;
    private final boolean processStderr;

    private AntPlugin(Builder builder) {
        super(builder);
        this.command = SystemCommand.newBuilder()
                .setCommand(builder.antBinary)
                .addArg(builder.buildTarget)
                .setBufferStdout(true)
                .setStdoutBufferLimit(builder.stdoutBufferLimit)
                .setBufferStderr(builder.processStderr)
                .setStderrBufferLimit(builder.stderrBufferLimit)
                .build();
        this.processStderr = builder.processStderr;
    }

    @Override
    protected boolean doExecute(Context context) throws Exception {
        SystemCommand.Output output = command.run(context);
        context.setOutput(output.getStdout());
        if (processStderr) {
            context.setErrors(output.getStderr());
        }
        return output.getStatus() == 0;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends Plugin.Builder<AntPlugin,Builder> {
        private String antBinary;
        private String buildTarget;
        private boolean processStderr;
        private int stdoutBufferLimit = SystemCommand.DEFAULT_BUFFER_SIZE;
        private int stderrBufferLimit = SystemCommand.DEFAULT_BUFFER_SIZE;

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

        public Builder setProcessStderr(boolean processStderr) {
            this.processStderr = processStderr;
            return this;
        }

        public Builder setStdoutBufferLimit(int stdoutBufferLimit) {
            this.stdoutBufferLimit = stdoutBufferLimit;
            return this;
        }

        public Builder setStderrBufferLimit(int stderrBufferLimit) {
            this.stderrBufferLimit = stderrBufferLimit;
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
