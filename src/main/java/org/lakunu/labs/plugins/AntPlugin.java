package org.lakunu.labs.plugins;

import com.google.common.base.Strings;
import org.lakunu.labs.utils.CommandOutputStream;
import org.lakunu.labs.utils.SystemCommand;

import static com.google.common.base.Preconditions.checkArgument;

public final class AntPlugin extends Plugin {

    private final String antBinary;
    private final String buildTarget;
    private final boolean processStderr;
    private final CommandOutputStream.Factory stdoutFactory;
    private final CommandOutputStream.Factory stderrFactory;

    private AntPlugin(Builder builder) {
        super(builder);
        checkArgument(!Strings.isNullOrEmpty(builder.antBinary), "antBinary is required");
        checkArgument(!Strings.isNullOrEmpty(builder.buildTarget), "buildTarget is required");
        this.antBinary = builder.antBinary;
        this.buildTarget = builder.buildTarget;
        this.processStderr = builder.processStderr;
        this.stdoutFactory = CommandOutputStream.newStreamFactory(
                true, true, builder.stdoutBufferLimit);
        this.stderrFactory = CommandOutputStream.newStreamFactory(
                false, builder.processStderr, builder.stderrBufferLimit);
    }

    @Override
    protected boolean doExecute(Context context) throws Exception {
        SystemCommand command = SystemCommand.newBuilder()
                .setCommand(antBinary)
                .addArgument(buildTarget)
                .setWorkingDirectory(context.getSubmissionDirectory())
                .build();
        CommandOutputStream stdout = stdoutFactory.build(context.getOutputHandler());
        CommandOutputStream stderr = stderrFactory.build(context.getOutputHandler());
        int status = command.run(stdout, stderr);
        context.setOutput(stdout.getContent());
        if (processStderr) {
            context.setErrors(stderr.getContent());
        }
        return status == 0;
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
