package org.lakunu.web.workers;

import org.apache.commons.io.FileUtils;
import org.lakunu.labs.Evaluation;
import org.lakunu.labs.Lab;
import org.lakunu.labs.ant.AntEvaluationPlan;
import org.lakunu.labs.submit.ArchiveSubmission;
import org.lakunu.labs.utils.BufferingOutputHandler;
import org.lakunu.web.models.EvaluationRecord;
import org.lakunu.web.models.Submission;
import org.lakunu.web.service.DAOFactory;
import org.lakunu.web.service.EvaluationJobWorker;

import java.io.File;
import java.nio.file.Files;

public final class SimpleWorker extends EvaluationJobWorker {

    public SimpleWorker(DAOFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    protected void doEvaluate(EvaluationRecord record, Submission submission) {
        File tempDir = null;
        try {
            tempDir = Files.createTempDirectory("lakunu_simple_eval_").toFile();
            logger.info("Created temporary directory: {}", tempDir.getPath());

            File submissionFile = new File(tempDir, "submission.zip");
            FileUtils.writeByteArrayToFile(submissionFile, submission.getData());
            logger.info("Wrote submission archive file to: {}", submissionFile.getPath());

            File labFile = new File(tempDir, "lab.xml");
            FileUtils.writeByteArrayToFile(labFile, record.getLab().getConfiguration());
            logger.info("Wrote lab configuration to: {}", labFile.getPath());

            new AntEvaluationPlan(labFile, null);
            Lab lab = Lab.newBuilder()
                    .setName(record.getLab().getName())
                    .setEvaluationPlan(new AntEvaluationPlan(labFile, null))
                    .build();
            BufferingOutputHandler outputHandler = new BufferingOutputHandler(64 * 1024);
            Evaluation evaluation = Evaluation.newBuilder()
                    .setSubmission(new ArchiveSubmission(submissionFile))
                    .setWorkingDirectory(FileUtils.getTempDirectory())
                    .setLab(lab)
                    .setCleanUpAfterFinish(true)
                    .setOutputHandler(outputHandler)
                    .build();
            evaluation.run();

            LabOutputParser outputParser = new LabOutputParser(new String(outputHandler.getBufferedOutput()));
            outputParser.getScores().forEach(s -> logger.info("Score: {} {}", s.getName(), s.toString()));
        } catch (Exception e) {
            logger.error("Error while evaluating submission: {}", submission.getId(), e);
        } finally {
            FileUtils.deleteQuietly(tempDir);
        }
    }

}
