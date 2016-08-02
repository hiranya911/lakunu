package org.lakunu.web.service;

import org.lakunu.web.models.Evaluation;
import org.lakunu.web.models.EvaluationRecord;
import org.lakunu.web.models.Submission;
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

        evaluate(submission);
    }

    private void evaluate(Submission submission) {
        logger.info("Processing submission: {}", submission.getId());
        Evaluation evaluation = Evaluation.newBuilder()
                .setSubmissionId(submission.getId())
                .setStartedAt(new Date())
                .build();
        EvaluationRecord record = daoFactory.getEvaluationDAO().startEvaluation(evaluation);
        doEvaluate(record, submission);
    }

    protected abstract void doEvaluate(EvaluationRecord record, Submission submission);
}
