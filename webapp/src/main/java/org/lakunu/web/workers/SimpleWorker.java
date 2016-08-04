package org.lakunu.web.workers;

import org.apache.commons.io.FileUtils;
import org.lakunu.labs.Evaluation;
import org.lakunu.labs.ant.AntEvaluationPlan;
import org.lakunu.labs.utils.BufferingOutputHandler;
import org.lakunu.web.models.EvaluationStatus;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Submission;
import org.lakunu.web.service.DAOFactory;
import org.lakunu.web.service.EvaluationJobWorker;
import org.lakunu.web.service.submissions.UserSubmission;
import org.lakunu.web.service.submissions.UserSubmissionFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;

public final class SimpleWorker extends EvaluationJobWorker {

    public SimpleWorker(DAOFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    protected EvaluationResult doEvaluate(Lab lab, Submission submission) {
        File tempDir = null;
        BufferingOutputHandler outputHandler = new BufferingOutputHandler(64 * 1024);
        boolean exception = false;
        try {
            tempDir = Files.createTempDirectory("lakunu_simple_eval_").toFile();
            logger.info("Created temporary directory: {}", tempDir.getPath());

            UserSubmission userSubmission = UserSubmissionFactory.create(submission.getType(),
                    submission.getData());
            File labFile = new File(tempDir, "lab.xml");
            FileUtils.writeByteArrayToFile(labFile, lab.getConfiguration());
            logger.info("Wrote lab configuration to: {}", labFile.getPath());

            new AntEvaluationPlan(labFile, null);
            org.lakunu.labs.Lab evaluationLab = org.lakunu.labs.Lab.newBuilder()
                    .setName(lab.getName())
                    .setEvaluationPlan(new AntEvaluationPlan(labFile, null))
                    .build();

            Evaluation evaluation = Evaluation.newBuilder()
                    .setSubmission(userSubmission.toSubmission(tempDir))
                    .setWorkingDirectory(FileUtils.getTempDirectory())
                    .setLab(evaluationLab)
                    .setCleanUpAfterFinish(true)
                    .setOutputHandler(outputHandler)
                    .setEndMarker(LabOutputParser.END_MARKER)
                    .build();
            evaluation.run();
        } catch (Exception e) {
            exception = true;
            logger.error("Error while evaluating submission: {}", submission.getId(), e);
        } finally {
            FileUtils.deleteQuietly(tempDir);
        }

        String logOutput = new String(outputHandler.getBufferedOutput());
        return newResultBuilder()
                .setLog(logOutput)
                .setScores(new LabOutputParser(logOutput).getScores())
                .setFinishingStatus(exception ? EvaluationStatus.FAILED : EvaluationStatus.SUCCESS)
                .build();
    }

}
