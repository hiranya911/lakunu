package org.lakunu.labs;

import org.apache.commons.io.FileUtils;
import org.lakunu.labs.config.JsonLabFactory;
import org.lakunu.labs.submit.DirectorySubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Usage: Main [config-path]");
            return;
        }

        File labConfig;
        if (args.length == 0) {
            labConfig = new File("lab.json").getAbsoluteFile();
        } else {
            labConfig = new File(args[0]).getAbsoluteFile();
        }

        logger.info("Lab configuration file: {}", labConfig.getPath());
        if (!labConfig.exists()) {
            logger.error("Lab configuration file does not exist");
            return;
        }

        Lab lab;
        try (FileInputStream in = FileUtils.openInputStream(labConfig)) {
            JsonLabFactory factory = new JsonLabFactory(in);
            lab = factory.build();
        } catch (IOException e) {
            logger.error("Error while loading configuration", e);
            return;
        }

        DirectorySubmission submission = new DirectorySubmission(
                "/Users/hiranya/academic/cs56/github-grader/target/source/lab00_EdieS");
        lab.run(submission);
    }

}
