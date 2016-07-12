package org.lakunu.labs.plugins;

import org.lakunu.labs.plugins.utils.SystemCommand;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public final class RunCommandPlugin extends Plugin {

    private final SystemCommand command;
    private final int status;

    private RunCommandPlugin(Builder builder) {
        super(builder);
        checkArgument(builder.status >= 0, "Invalid status: %s", builder.status);
        this.command = SystemCommand.newBuilder()
                .setCommand(builder.command)
                .addArgs(builder.args)
                .setBufferStdout(true)
                .setStdoutBufferLimit(builder.stdoutBufferLimit)
                .setBufferStderr(true)
                .setStderrBufferLimit(builder.stderrBufferLimit)
                .build();
        this.status = builder.status;
    }

    @Override
    protected boolean doExecute(Context context) throws Exception {
        SystemCommand.Output output = command.run(context);
        context.setOutput(output.getStdout());
        context.setErrors(output.getStderr());
        outputNewLine(context);
        return output.getStatus() == this.status;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends Plugin.Builder<RunCommandPlugin,Builder> {
        private String command;
        private final List<String> args = new ArrayList<>();
        private int status = 0;
        private int stdoutBufferLimit = SystemCommand.DEFAULT_BUFFER_SIZE;
        private int stderrBufferLimit = SystemCommand.DEFAULT_BUFFER_SIZE;

        private Builder() {
        }

        public Builder setCommand(String command) {
            this.command = command;
            return this;
        }

        public Builder addArgument(String arg) {
            this.args.add(arg);
            return this;
        }

        public Builder setStatus(int status) {
            this.status = status;
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
        public RunCommandPlugin build() {
            return new RunCommandPlugin(this);
        }
    }
}
