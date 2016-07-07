package org.lakunu.labs.plugins;

import org.lakunu.labs.utils.SystemCommand;

public final class AntPlugin extends Plugin {

    private final SystemCommand command;

    private AntPlugin(Builder builder) {
        super(builder);
        this.command = SystemCommand.newBuilder()
                .setCommand(builder.antBinary)
                .addArgument(builder.buildTarget)
                .setBufferStdout(true)
                .setStdoutBufferLimit(builder.stdoutBufferLimit)
                .setBufferStderr(builder.processStderr)
                .setStderrBufferLimit(builder.stderrBufferLimit)
                .build();
    }

    @Override
    protected boolean doExecute(Context context) throws Exception {
        SystemCommand.Output output = command.run(context.getSubmissionDirectory(),
                context.getOutputHandler());
        context.setOutput(output.getStdout());
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
