package org.lakunu.labs.plugins;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.labs.EvaluationContext;
import org.lakunu.labs.utils.SystemCommand;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public final class RunCommandPlugin extends Plugin {

    private final String command;
    private final ImmutableList<String> args;
    private final int status;

    private RunCommandPlugin(Builder builder) {
        super(builder);
        checkArgument(!Strings.isNullOrEmpty(builder.command), "Command is required");
        checkArgument(builder.status >= 0, "Invalid status: %s", builder.status);
        this.command = builder.command;
        this.args = ImmutableList.copyOf(builder.args);
        this.status = builder.status;
    }

    @Override
    protected boolean doExecute(EvaluationContext context) throws Exception {
        SystemCommand.Builder builder = SystemCommand.newBuilder()
                .setCommand(command)
                .setOutputHandler(context.getOutputHandler())
                .setWorkingDir(context.getSubmissionDirectory());
        args.forEach(builder::addArgument);
        SystemCommand command = builder.build();
        return command.run() == status;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends Plugin.Builder<RunCommandPlugin,Builder> {
        private String command;
        private final List<String> args = new ArrayList<>();
        private int status = 0;

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
