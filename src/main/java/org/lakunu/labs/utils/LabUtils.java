package org.lakunu.labs.utils;

import org.lakunu.labs.LabOutputHandler;

public class LabUtils {

    public static void outputTitle(String title, LabOutputHandler outputHandler) {
        outputHandler.info("");
        outputHandler.info("------------------------------------------------------------------------");
        outputHandler.info(title);
        outputHandler.info("------------------------------------------------------------------------");
        outputHandler.info("");
    }

}
