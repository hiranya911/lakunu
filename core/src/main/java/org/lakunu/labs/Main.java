package org.lakunu.labs;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.config.JsonLabFactory;
import org.lakunu.labs.submit.DirectorySubmission;
import org.lakunu.labs.utils.FileOutputHandler;
import org.lakunu.labs.utils.LoggingOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

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
                    .build())
            .addOption(Option.builder("o").longOpt("output")
                    .desc("Path to the output file (optional)")
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

        LabOutputHandler outputHandler;
        try {
            outputHandler = getOutputHandler(cmd);
        } catch (IOException e) {
            logger.error("Error while initializing output handler", e);
            return;
        }

        DirectorySubmission submission = new DirectorySubmission(remaining[0]);
        try (FileInputStream in = FileUtils.openInputStream(getLabConfig(cmd))) {
            Evaluation evaluation = Evaluation.newBuilder().setSubmission(submission)
                    .setLab(JsonLabFactory.newLab(in))
                    .setWorkingDirectory(getWorkingDirectory(cmd))
                    .setCleanUpAfterFinish(true)
                    .setOutputHandler(outputHandler)
                    .build();
            evaluation.run();
        } catch (IOException e) {
            logger.error("Error while evaluating lab", e);
        } finally {
            outputHandler.close();
        }
    }

    private static File getLabConfig(CommandLine cmd) {
        File labConfig;
        if (cmd.hasOption("l")) {
            labConfig = new File(cmd.getOptionValue("l")).getAbsoluteFile();
        } else {
            labConfig = new File("lab.json").getAbsoluteFile();
        }
        logger.info("Lab configuration file: {}", labConfig.getPath());
        checkArgument(labConfig.exists() && labConfig.isFile(),
                "lab config does not exist or is not a regular file");
        return labConfig;
    }

    private static File getWorkingDirectory(CommandLine cmd) {
        if (cmd.hasOption("wd")) {
            return new File(cmd.getOptionValue("wd")).getAbsoluteFile();
        } else {
            return FileUtils.getTempDirectory();
        }
    }

    private static LabOutputHandler getOutputHandler(CommandLine cmd) throws IOException {
        if (cmd.hasOption("o")) {
            return new FileOutputHandler(cmd.getOptionValue("o"));
        } else {
            return new LoggingOutputHandler();
        }
    }

}
