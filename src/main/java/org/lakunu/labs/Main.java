package org.lakunu.labs;

import org.apache.commons.cli.*;
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

    private static final Options OPTIONS = new Options()
            .addOption(Option.builder("l").longOpt("lab")
                    .desc("Path to the lab configuration file")
                    .hasArg().argName("FILE")
                    .build());

    private static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("lakunu [OPTIONS] DIRECTORY", "Options", OPTIONS, "");
    }

    public static void main(String[] args) {
        CommandLine cmd;
        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(OPTIONS, args);
        } catch (ParseException e) {
            printUsage();
            return;
        }

        String[] remaining = cmd.getArgs();
        if (remaining.length != 1) {
            printUsage();
            return;
        }

        File labConfig;
        if (cmd.hasOption("l")) {
            labConfig = new File(cmd.getOptionValue("l")).getAbsoluteFile();
        } else {
            labConfig = new File("lab.json").getAbsoluteFile();
        }

        logger.info("Lab configuration file: {}", labConfig.getPath());
        if (!labConfig.exists()) {
            logger.error("Lab configuration file does not exist");
            return;
        }

        DirectorySubmission submission = new DirectorySubmission(remaining[0]);
        try (FileInputStream in = FileUtils.openInputStream(labConfig)) {
            JsonLabFactory factory = new JsonLabFactory(in);
            SubmissionEvaluator evaluator = new SubmissionEvaluator(submission, factory.build());
            evaluator.evaluate(FileUtils.getTempDirectory());
        } catch (IOException e) {
            logger.error("Error while evaluating lab", e);
        }
    }

}
