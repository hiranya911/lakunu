package org.lakunu.labs;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public final class TestOutputHandler implements LabOutputHandler {

    private final List<LogEntry> entries = new ArrayList<>();

    public void info(String line) {
        entries.add(new LogEntry(line, Level.INFO));
    }

    public void warn(String line) {
        entries.add(new LogEntry(line, Level.WARN));
    }

    public void error(String line) {
        entries.add(new LogEntry(line, Level.ERROR));
    }


    public ImmutableList<LogEntry> entries() {
        return ImmutableList.copyOf(entries);
    }

    public static class LogEntry {
        public final String line;
        public final Level level;

        LogEntry(String line, Level level) {
            this.line = line;
            this.level = level;
        }
    }

    public enum Level {
        INFO,
        WARN,
        ERROR
    }
}
