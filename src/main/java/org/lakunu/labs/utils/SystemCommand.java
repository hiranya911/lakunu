package org.lakunu.labs.utils;

import com.google.common.base.Strings;
import org.apache.commons.exec.*;
import org.lakunu.labs.LabOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class SystemCommand {

    private static final Logger logger = LoggerFactory.getLogger(SystemCommand.class);

    public static final int DEFAULT_BUFFER_SIZE = 64 * 1024;

    private final CommandLine cmdLine;
    private final CommandOutputStream.Factory stdoutFactory;
    private final CommandOutputStream.Factory stderrFactory;

    private SystemCommand(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.command), "Command is required");
        this.cmdLine = CommandLine.parse(builder.command);
        builder.args.forEach(arg -> {
            checkArgument(!Strings.isNullOrEmpty(arg), "args cannot be null or empty");
            this.cmdLine.addArgument(arg);

        });
        this.stdoutFactory = newStreamFactory(true, builder.bufferStdout, builder.stdoutBufferLimit);
        this.stderrFactory = newStreamFactory(false, builder.bufferStderr, builder.stderrBufferLimit);
    }

    public Output run(LabOutputHandler outputHandler) throws IOException {
        return run(new File("."), outputHandler);
    }

    public Output run(File workingDir, LabOutputHandler outputHandler) throws IOException {
        checkNotNull(workingDir, "workingDirectory is required");
        checkArgument(workingDir.exists() && workingDir.isDirectory(),
                "working directory does not exist or is not a directory");
        final CommandOutputStream stdout = stdoutFactory.build(outputHandler);
        final CommandOutputStream stderr = stderrFactory.build(outputHandler);
        Executor exec = new DefaultExecutor();
        exec.setExitValues(null);
        exec.setWorkingDirectory(workingDir);
        exec.setStreamHandler(new PumpStreamHandler(stdout, stderr));
        if (logger.isDebugEnabled()) {
            logger.debug("Command: {}; Working dir: {}", cmdLine.toString(), workingDir);
        }
        return new Output(exec.execute(cmdLine), stdout, stderr);
    }

    private CommandOutputStream.Factory newStreamFactory(boolean stdout, boolean buffering, int threshold) {
        if (buffering) {
            return CommandOutputStream.withBuffering(stdout, threshold);
        } else {
            return CommandOutputStream.withoutBuffering(stdout);
        }
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

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String command;
        private final List<String> args = new ArrayList<>();
        private boolean bufferStdout;
        private boolean bufferStderr;
        private int stdoutBufferLimit = DEFAULT_BUFFER_SIZE;
        private int stderrBufferLimit = DEFAULT_BUFFER_SIZE;

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

        public Builder addArguments(Collection<String> args) {
            this.args.addAll(args);
            return this;
        }

        public Builder setBufferStdout(boolean bufferStdout) {
            this.bufferStdout = bufferStdout;
            return this;
        }

        public Builder setBufferStderr(boolean bufferStderr) {
            this.bufferStderr = bufferStderr;
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

        public SystemCommand build() {
            return new SystemCommand(this);
        }
    }
}
