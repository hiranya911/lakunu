package org.lakunu.labs.utils;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.commons.exec.*;
import org.lakunu.labs.LabOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class SystemCommand {

    private static final Logger logger = LoggerFactory.getLogger(SystemCommand.class);

    private final String command;
    private final String[] args;
    private final File workingDir;
    private final LabOutputHandler outputHandler;
    private final Queue<String> stdoutBuffer;
    private final Queue<String> stderrBuffer;

    private SystemCommand(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.command), "Command is required");
        checkNotNull(builder.workingDir, "Working directory is required");
        checkArgument(builder.workingDir.isDirectory() && builder.workingDir.exists(),
                "Working directory path is not a directory or does not exist");
        checkNotNull(builder.outputHandler, "Output handler is required");
        this.command = builder.command;
        this.args = builder.args.toArray(new String[builder.args.size()]);
        this.workingDir = builder.workingDir;
        this.outputHandler = builder.outputHandler;
        this.stdoutBuffer = builder.bufferStdout ? new ConcurrentLinkedQueue<>() : null;
        this.stderrBuffer = builder.bufferStderr ? new ConcurrentLinkedQueue<>() : null;
    }

    public int run() throws IOException {
        CommandLine cmdLine = CommandLine.parse(command);
        for (String arg : args) {
            cmdLine.addArgument(arg);
        }
        Executor exec = new DefaultExecutor();
        exec.setExitValues(null);
        exec.setWorkingDirectory(workingDir);
        exec.setStreamHandler(new PumpStreamHandler(new CommandOutputStream(true),
                new CommandOutputStream(false)));
        if (logger.isDebugEnabled()) {
            logger.debug("Command: {}; Working dir: {}", cmdLine.toString(), workingDir);
        }
        return exec.execute(cmdLine);
    }

    public ImmutableList<String> getStdout() {
        checkState(stdoutBuffer != null, "Buffering not enabled for stdout");
        return ImmutableList.copyOf(stdoutBuffer);
    }

    public ImmutableList<String> getStderr() {
        checkState(stderrBuffer != null, "Buffering not enabled for stderr");
        return ImmutableList.copyOf(stderrBuffer);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private static final File CURRENT_DIR = new File(".");

    private final class CommandOutputStream extends LogOutputStream {
        private final boolean stdout;

        private CommandOutputStream(boolean stdout) {
            this.stdout = stdout;
        }

        @Override
        protected void processLine(String line, int level) {
            if (stdout) {
                if (stdoutBuffer != null) {
                    stdoutBuffer.offer(line);
                }
                outputHandler.info(line);
            } else {
                if (stderrBuffer != null) {
                    stderrBuffer.offer(line);
                }
                outputHandler.error(line);
            }
        }
    }

    public static class Builder {

        private String command;
        private final List<String> args = new ArrayList<>();
        private File workingDir = CURRENT_DIR;
        private LabOutputHandler outputHandler;
        private boolean bufferStdout;
        private boolean bufferStderr;

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

        public SystemCommand build() {
            return new SystemCommand(this);
        }
    }
}
