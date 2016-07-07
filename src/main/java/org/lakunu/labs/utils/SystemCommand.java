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

    private final CommandLine cmdLine;
    private final File workingDir;
    private final CommandOutputStream.Factory stdoutFactory;
    private final CommandOutputStream.Factory stderrFactory;


    private SystemCommand(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.command), "Command is required");
        checkNotNull(builder.workingDir, "Working directory is required");
        checkArgument(builder.workingDir.isDirectory() && builder.workingDir.exists(),
                "Working directory path is not a directory or does not exist");
        this.cmdLine = CommandLine.parse(builder.command);
        builder.args.forEach(this.cmdLine::addArgument);
        this.workingDir = builder.workingDir;
        this.stdoutFactory = newStreamFactory(true, builder.outputHandler, builder.bufferStdout,
                builder.stdoutBufferLimit);
        this.stderrFactory = newStreamFactory(false, builder.outputHandler, builder.bufferStderr,
                builder.stderrBufferLimit);
    }

    public Output run() throws IOException {
        final CommandOutputStream stdout = stdoutFactory.build();
        final CommandOutputStream stderr = stderrFactory.build();
        Executor exec = new DefaultExecutor();
        exec.setExitValues(null);
        exec.setWorkingDirectory(workingDir);
        exec.setStreamHandler(new PumpStreamHandler(stdout, stderr));
        if (logger.isDebugEnabled()) {
            logger.debug("Command: {}; Working dir: {}", cmdLine.toString(), workingDir);
        }
        return new Output(exec.execute(cmdLine), stdout, stderr);
    }

    private CommandOutputStream.Factory newStreamFactory(boolean stdout, LabOutputHandler outputHandler,
                                          boolean buffering, int threshold) {
        if (buffering) {
            return CommandOutputStream.withBuffering(stdout, outputHandler, threshold);
        } else {
            return CommandOutputStream.withoutBuffering(stdout, outputHandler);
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
