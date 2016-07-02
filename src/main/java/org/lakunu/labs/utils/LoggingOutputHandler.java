package org.lakunu.labs.utils;

import org.lakunu.labs.LabOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggingOutputHandler implements LabOutputHandler {

    private static final Logger logger = LoggerFactory.getLogger("LAB_OUTPUT");

    @Override
    public void processLine(String line, Level level) {
        switch (level) {
            case INFO:
                logger.info(line);
                break;
            case WARN:
                logger.warn(line);
                break;
            case ERROR:
                logger.error(line);
                break;
        }
    }
}
