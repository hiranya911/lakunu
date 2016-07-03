package org.lakunu.labs.utils;

import org.lakunu.labs.LabOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggingOutputHandler implements LabOutputHandler {

    private static final Logger logger = LoggerFactory.getLogger("LAB_OUTPUT");

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

}
