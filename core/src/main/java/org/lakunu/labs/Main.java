package org.lakunu.labs;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Booleans;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.ant.AntEvaluationPlan;
import org.lakunu.labs.resources.DirectoryResourceCollection;
import org.lakunu.labs.resources.Resources;
import org.lakunu.labs.submit.DirectorySubmission;
import org.lakunu.labs.utils.LoggingOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final Options OPTIONS = new Options()
            .addOption(Option.builder("l").longOpt("lab")
                    .desc("Path to the lab configuration file (defaults to ./lakunu.xml)")
                    .hasArg().argName("FILE")
                    .build())
            .addOption(Option.builder("c").longOpt("collection")
                    .desc("Path to a directory containing lab resources")
                    .hasArg().argName("DIR")
                    .build())
            .addOption(Option.builder("r").longOpt("resources")
                    .desc("List of resource files")
                    .hasArgs().argName("FILES")
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

        Lab lab = Lab.newBuilder()
                .setName("anonymous")
                .setResources(getResources(cmd))
                .setEvaluationPlan(new AntEvaluationPlan(getLabConfig(cmd), null))
                .build();

        logger.info("Evaluating directory submission: {}", remaining[0]);
        DirectorySubmission submission = new DirectorySubmission(remaining[0]);
        Evaluation evaluation = Evaluation.newBuilder().setSubmission(submission)
                .setLab(lab)
                .setWorkingDirectory(getWorkingDirectory(cmd))
                .setCleanUpAfterFinish(true)
                .setOutputHandler(LoggingOutputHandler.DEFAULT)
                .build();
        evaluation.run();
    }

    private static File getLabConfig(CommandLine cmd) {
        File labConfig;
        if (cmd.hasOption("l")) {
            labConfig = new File(cmd.getOptionValue("l")).getAbsoluteFile();
        } else {
            labConfig = new File("lakunu.xml").getAbsoluteFile();
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

    private static Resources getResources(CommandLine cmd) {
        int options = Booleans.countTrue(cmd.hasOption("r"), cmd.hasOption("c"));
        checkArgument(options < 2, "Cannot specify both resource files and a collection");
        if (cmd.hasOption("c")) {
            return new Resources(new DirectoryResourceCollection(cmd.getOptionValue("c")));
        }

        ImmutableSet.Builder<File> resources = ImmutableSet.builder();
        if (cmd.hasOption("r")) {
            String[] resourcePaths = cmd.getOptionValues("r");
            for (String path : resourcePaths) {
                resources.add(new File(path));
            }
        }
        return new Resources(resources.build());
    }

}
