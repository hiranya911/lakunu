package org.lakunu.labs.utils;

import com.google.common.base.Strings;
import org.apache.commons.exec.*;
import org.lakunu.labs.LabOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class SystemCommand {

    private static final Logger logger = LoggerFactory.getLogger(SystemCommand.class);

    public static final int DEFAULT_BUFFER_SIZE = 64 * 1024;

    private final String command;
    private final String[] args;
    private final File workingDir;
    private final CommandOutputStream stdout;
    private final CommandOutputStream stderr;

    private SystemCommand(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.command), "Command is required");
        checkNotNull(builder.workingDir, "Working directory is required");
        checkArgument(builder.workingDir.isDirectory() && builder.workingDir.exists(),
                "Working directory path is not a directory or does not exist");
        checkNotNull(builder.outputHandler, "Output handler is required");
        checkArgument(builder.stdoutBufferLimit >= 0, "Buffer limit cannot be negative");
        checkArgument(builder.stderrBufferLimit >= 0, "Buffer limit cannot be negative");
        this.command = builder.command;
        this.args = builder.args.toArray(new String[builder.args.size()]);
        this.workingDir = builder.workingDir;
        this.stdout = newStream(true, builder.outputHandler, builder.bufferStdout,
                builder.stdoutBufferLimit);
        this.stderr = newStream(false, builder.outputHandler, builder.bufferStderr,
                builder.stderrBufferLimit);
    }

    public int run() throws IOException {
        CommandLine cmdLine = CommandLine.parse(command);
        for (String arg : args) {
            cmdLine.addArgument(arg);
        }
        Executor exec = new DefaultExecutor();
        exec.setExitValues(null);
        exec.setWorkingDirectory(workingDir);
        exec.setStreamHandler(new PumpStreamHandler(stdout, stderr));
        if (logger.isDebugEnabled()) {
            logger.debug("Command: {}; Working dir: {}", cmdLine.toString(), workingDir);
        }
        return exec.execute(cmdLine);
    }

    public String getStdout() {
        return stdout.getContent();
    }

    public String getStderr() {
        return stderr.getContent();
    }

    private CommandOutputStream newStream(boolean stdout, LabOutputHandler outputHandler,
                                          boolean buffering, int threshold) {
        if (buffering) {
            return CommandOutputStream.withBuffering(stdout, outputHandler, threshold);
        } else {
            return CommandOutputStream.withoutBuffering(stdout, outputHandler);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private static final File CURRENT_DIR = new File(".");

    public static class Builder {

        private String command;
        private final List<String> args = new ArrayList<>();
        private File workingDir = CURRENT_DIR;
        private LabOutputHandler outputHandler;
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

        public Builder setWorkingDir(File workingDir) {
            this.workingDir = workingDir;
            return this;
        }

        public Builder setOutputHandler(LabOutputHandler outputHandler) {
            this.outputHandler = outputHandler;
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
