package org.lakunu.web.service;

import org.lakunu.web.dao.EvaluationDAO;
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

        logger.info("Processing submission: {}", submission.getId());
        EvaluationDAO evaluationDAO = daoFactory.getEvaluationDAO();
        EvaluationRecord record = evaluationDAO.startEvaluation(submission.getId(), new Date());
        EvaluationRecord.Completion finish = doEvaluate(record, submission);
        evaluationDAO.finishEvaluation(finish.apply());
        logger.info("Finished evaluating submission: {}", submission.getId());
    }

    protected abstract EvaluationRecord.Completion doEvaluate(EvaluationRecord record,
                                                              Submission submission);
}
