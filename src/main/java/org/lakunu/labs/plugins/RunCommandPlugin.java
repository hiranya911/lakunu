package org.lakunu.labs.plugins;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.labs.utils.CommandOutputStream;
import org.lakunu.labs.utils.SystemCommand;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public final class RunCommandPlugin extends Plugin {

    private final int status;
    private final String command;
    private final ImmutableList<String> args;
    private final CommandOutputStream.Factory stdoutFactory;
    private final CommandOutputStream.Factory stderrFactory;

    private RunCommandPlugin(Builder builder) {
        super(builder);
        checkArgument(!Strings.isNullOrEmpty(builder.command), "Command is required");
        checkArgument(builder.status >= 0, "Invalid status: %s", builder.status);
        this.command = builder.command;
        this.args = ImmutableList.copyOf(builder.args);
        this.status = builder.status;
        this.stdoutFactory = CommandOutputStream.newStreamFactory(
                true, true, builder.stdoutBufferLimit);
        this.stderrFactory = CommandOutputStream.newStreamFactory(
                false, true, builder.stderrBufferLimit);
    }

    @Override
    protected boolean doExecute(Context context) throws Exception {
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand(command)
                .addArguments(args)
                .setWorkingDirectory(context.getSubmissionDirectory())
                .build();

        CommandOutputStream stdout = stdoutFactory.build(context.getOutputHandler());
        CommandOutputStream stderr = stderrFactory.build(context.getOutputHandler());
        int status = cmd.run(stdout, stderr);
        context.setOutput(stdout.getContent());
        context.setErrors(stderr.getContent());
        return status == this.status;
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
