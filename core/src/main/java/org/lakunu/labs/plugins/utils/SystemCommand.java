package org.lakunu.labs.plugins.utils;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.lakunu.labs.plugins.Plugin;

import java.io.IOException;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

public final class SystemCommand {

    public static final int DEFAULT_BUFFER_SIZE = 64 * 1024;

    private final String command;
    private final ImmutableList<String> args;
    private final CommandOutputStream.Factory stdoutFactory;
    private final CommandOutputStream.Factory stderrFactory;

    private SystemCommand(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.command), "command is required");
        this.command = builder.command;
        this.args = builder.args.build();
        this.stdoutFactory = CommandOutputStream.newStreamFactory(true, builder.bufferStdout,
                builder.stdoutBufferLimit);
        this.stderrFactory = CommandOutputStream.newStreamFactory(false, builder.bufferStderr,
                builder.stderrBufferLimit);
    }

    public Output run(Plugin.Context context) throws IOException {
        CommandLine cmdLine = CommandLine.parse(command);
        args.forEach(cmdLine::addArgument);
        Executor exec = new DefaultExecutor();
        exec.setExitValues(null);
        exec.setWorkingDirectory(context.getSubmissionDirectory());
        CommandOutputStream stdout = stdoutFactory.build(context.getOutputHandler());
        CommandOutputStream stderr = stderrFactory.build(context.getOutputHandler());
        exec.setStreamHandler(new PumpStreamHandler(stdout, stderr));
        return new Output(exec.execute(cmdLine), stdout, stderr);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Output {
        private final int status;
        private final CommandOutputStream stdout;
        private final CommandOutputStream stderr;

        private Output(int status, CommandOutputStream stdout, CommandOutputStream stderr) {
            this.status = status;
            this.stdout = stdout;
            this.stderr = stderr;
        }

        public int getStatus() {
            return status;
        }

        public String getStdout() {
            return stdout.getContent();
        }

        public String getStderr() {
            return stderr.getContent();
        }
    }

    public static class Builder {

        private String command;
        private final ImmutableList.Builder<String> args = ImmutableList.builder();
        private boolean bufferStdout;
        private int stdoutBufferLimit = DEFAULT_BUFFER_SIZE;
        private boolean bufferStderr;
        private int stderrBufferLimit = DEFAULT_BUFFER_SIZE;

        private Builder() {
        }

        public Builder setCommand(String command) {
            this.command = command;
            return this;
        }

        public Builder addArgs(Collection<String> args) {
            this.args.addAll(args);
            return this;
        }

        public Builder addArg(String arg) {
            this.args.add(arg);
            return this;
        }

        public Builder setBufferStdout(boolean bufferStdout) {
            this.bufferStdout = bufferStdout;
            return this;
        }

        public Builder setStdoutBufferLimit(int stdoutBufferLimit) {
            this.stdoutBufferLimit = stdoutBufferLimit;
            return this;
        }

        public Builder setBufferStderr(boolean bufferStderr) {
            this.bufferStderr = bufferStderr;
            return this;
        }

        public Builder setStderrBufferLimit(int stderrBufferLimit) {
            this.stderrBufferLimit = stderrBufferLimit;
            return this;
        }

        public SystemCommand build() {
            return new SystemCommand(this);
        }
    }

}
