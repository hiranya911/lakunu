package org.lakunu.labs;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.config.JsonLabFactory;
import org.lakunu.labs.submit.DirectorySubmission;
import org.lakunu.labs.utils.LoggingOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final Options OPTIONS = new Options()
            .addOption(Option.builder("l").longOpt("lab")
                    .desc("Path to the lab configuration file (defaults to ./lab.json)")
                    .hasArg().argName("FILE")
                    .build())
            .addOption(Option.builder("wd").longOpt("working-dir")
                    .desc("Path to the working directory (defaults to system's temp directory)")
                    .hasArg().argName("DIR")
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

        logger.info("Evaluating directory submission: {}", remaining[0]);
        DirectorySubmission submission = new DirectorySubmission(remaining[0]);
        try (FileInputStream in = FileUtils.openInputStream(getLabConfig(cmd))) {
            Evaluation evaluation = Evaluation.newBuilder().setSubmission(submission)
                    .setLab(JsonLabFactory.newLab(in))
                    .setWorkingDirectory(getWorkingDirectory(cmd))
                    .setCleanUpAfterFinish(true)
                    .setOutputHandler(LoggingOutputHandler.DEFAULT)
                    .build();
            evaluation.run();
        } catch (IOException e) {
            logger.error("Error while evaluating lab", e);
        }
    }

    private static File getLabConfig(CommandLine cmd) {
        File labConfig;
        if (cmd.hasOption("l")) {
            labConfig = new File(cmd.getOptionValue("l")).getAbsoluteFile();
        } else {
            labConfig = new File("lab.json").getAbsoluteFile();
        }
        logger.info("Loading lab configuration from: {}", labConfig.getPath());
        return labConfig;
    }

    private static File getWorkingDirectory(CommandLine cmd) {
        File workingDir;
        if (cmd.hasOption("wd")) {
            workingDir = new File(cmd.getOptionValue("wd")).getAbsoluteFile();
        } else {
            workingDir = FileUtils.getTempDirectory();
        }
        logger.info("Using working directory: {}", workingDir.getAbsolutePath());
        return workingDir;
    }

}
