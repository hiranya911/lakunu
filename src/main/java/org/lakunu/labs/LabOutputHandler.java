package org.lakunu.labs;

public interface LabOutputHandler {

    enum Level {
        INFO,
        WARN,
        ERROR
    }

    void processLine(String line, Level level);

}
