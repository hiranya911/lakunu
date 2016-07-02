package org.lakunu.labs.utils;

import com.google.common.base.Strings;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.lakunu.labs.LabOutputHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class SystemCommand {

    private final String command;
    private final String[] args;
    private final File workingDir;
    private final OutputStream inputStreamHandler;
    private final OutputStream errorStreamHandler;

    private SystemCommand(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.command), "Command is required");
        checkNotNull(builder.workingDir, "Working directory is required");
        checkArgument(builder.workingDir.isDirectory() && builder.workingDir.exists(),
                "Working directory path is not a directory or does not exist");
        checkNotNull(builder.outputHandler, "Output handler is required");
        this.command = builder.command;
        this.args = builder.args.toArray(new String[builder.args.size()]);
        this.workingDir = builder.workingDir;
        this.inputStreamHandler = LabOutputHandlerStream.infoLogger(builder.outputHandler);
        this.errorStreamHandler = LabOutputHandlerStream.errorLogger(builder.outputHandler);
    }

    public int run() throws IOException {
        CommandLine cmdLine = CommandLine.parse(command);
        for (String arg : args) {
            cmdLine.addArgument(arg);
        }
        Executor exec = new DefaultExecutor();
        exec.setWorkingDirectory(workingDir);
        exec.setStreamHandler(new PumpStreamHandler(inputStreamHandler, errorStreamHandler));
        return exec.execute(cmdLine);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String command;
        private final List<String> args = new ArrayList<>();
        private File workingDir;
        private LabOutputHandler outputHandler = new LoggingOutputHandler();

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

        public SystemCommand build() {
            return new SystemCommand(this);
        }
    }
}
