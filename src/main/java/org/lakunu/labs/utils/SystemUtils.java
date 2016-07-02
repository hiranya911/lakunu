package org.lakunu.labs.utils;

import org.apache.commons.exec.*;
import java.io.File;
import java.io.IOException;


public class SystemUtils {

    public static int runCommand(File currentDir, String command, String... args) throws IOException {
        CommandLine cmdLine = CommandLine.parse(command);
        for (String arg : args) {
            cmdLine.addArgument(arg);
        }
        Executor exec = new DefaultExecutor();
        exec.setWorkingDirectory(currentDir);
        exec.setStreamHandler(new PumpStreamHandler(new StdoutLogOutputStream()));
        return exec.execute(cmdLine);
    }

    static class StdoutLogOutputStream extends LogOutputStream {
        @Override
        protected void processLine(String line, int logLevel) {
            System.out.println("[output] " + line);
        }
    }

}
