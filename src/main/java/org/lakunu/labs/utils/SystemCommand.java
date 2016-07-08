package org.lakunu.labs.utils;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.commons.exec.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class SystemCommand {

    private static final Logger logger = LoggerFactory.getLogger(SystemCommand.class);

    public static final int DEFAULT_BUFFER_SIZE = 64 * 1024;

    private final String command;
    private final ImmutableList<String> args;
    private final File workingDirectory;

    private SystemCommand(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.command), "Command is required");
        checkNotNull(builder.workingDirectory, "workingDirectory is required");
        checkArgument(builder.workingDirectory.exists() && builder.workingDirectory.isDirectory(),
                "working directory does not exist or is not a directory");
        this.command = builder.command;
        this.args = ImmutableList.copyOf(builder.args);
        this.workingDirectory = builder.workingDirectory;
    }

    public int run(OutputStream stdout, OutputStream stderr) throws IOException {
        checkNotNull(stdout, "stdout stream is required");
        checkNotNull(stderr, "stderr stream is required");
        CommandLine cmdLine = CommandLine.parse(command);
        args.forEach(cmdLine::addArgument);
        Executor exec = new DefaultExecutor();
        exec.setExitValues(null);
        exec.setWorkingDirectory(workingDirectory);
        exec.setStreamHandler(new PumpStreamHandler(stdout, stderr));
        if (logger.isDebugEnabled()) {
            logger.debug("Command: {}; Working dir: {}", cmdLine.toString(), workingDirectory);
        }
        return exec.execute(cmdLine);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String command;
        private final List<String> args = new ArrayList<>();
        private File workingDirectory = FileUtils.getTempDirectory();

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

        public Builder setWorkingDirectory(File workingDirectory) {
            this.workingDirectory = workingDirectory;
            return this;
        }

        public SystemCommand build() {
            return new SystemCommand(this);
        }
    }
}
