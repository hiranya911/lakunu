package org.lakunu.web.service;

import com.google.common.collect.ImmutableList;
import org.lakunu.labs.Score;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class EvaluationJobWorker {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final DAOFactory daoFactory;

    public EvaluationJobWorker(DAOFactory daoFactory) {
        checkNotNull(daoFactory, "DAOFactory is required");
        this.daoFactory = daoFactory;
    }

    public final void evaluate(String submissionId) {
        Submission submission = daoFactory.getSubmissionDAO().getSubmission(submissionId);
        if (submission == null) {
            logger.warn("No submission available by ID: {}", submissionId);
            return;
        }

        logger.info("Processing submission: {}", submission.getId());
        Date startedAt = new Date();
        Lab lab = daoFactory.getEvaluationDAO().getLabForEvaluation(submission);
        if (lab == null) {
            logger.warn("No lab available by ID: {}", submission.getLabId());
            return;
        }

        EvaluationResult result = doEvaluate(lab, submission);
        Evaluation evaluation = Evaluation.newBuilder()
                .setSubmissionId(submission.getId())
                .setStartedAt(startedAt)
                .setFinishedAt(new Date())
                .setFinishingStatus(result.finishingStatus)
                .addScores(result.scores)
                .setLog(result.log)
                .build();
        if (daoFactory.getEvaluationDAO().addEvaluation(evaluation, lab)) {
            logger.info("Finished evaluating submission: {}", submission.getId());
        } else {
            logger.warn("Failed to save evaluation record. The Lab has been updated.");
            throw new DAOException("Update to lab detected - Attempt retry");
        }
    }

    protected abstract EvaluationResult doEvaluate(Lab lab, Submission submission);

    public static class EvaluationResult {

        private final String log;
        private final EvaluationStatus finishingStatus;
        private final ImmutableList<Score> scores;

        private EvaluationResult(EvaluationResultBuilder builder) {
            this.log = builder.log;
            this.finishingStatus = builder.finishingStatus;
            this.scores = builder.scores;
        }
    }

    public static EvaluationResultBuilder newResultBuilder() {
        return new EvaluationResultBuilder();
    }

    public static class EvaluationResultBuilder {

        private String log;
        private EvaluationStatus finishingStatus;
        private ImmutableList<Score> scores;

        private EvaluationResultBuilder() {
        }

        public EvaluationResultBuilder setLog(String log) {
            this.log = log;
            return this;
        }

        public EvaluationResultBuilder setFinishingStatus(EvaluationStatus finishingStatus) {
            this.finishingStatus = finishingStatus;
            return this;
        }

        public EvaluationResultBuilder setScores(ImmutableList<Score> scores) {
            this.scores = scores;
            return this;
        }

        public EvaluationResult build() {
            return new EvaluationResult(this);
        }
    }
}
