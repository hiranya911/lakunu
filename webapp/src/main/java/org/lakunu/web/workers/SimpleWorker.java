package org.lakunu.web.workers;

import org.lakunu.web.models.Submission;
import org.lakunu.web.service.DAOFactory;
import org.lakunu.web.service.EvaluationJobWorker;

public final class SimpleWorker extends EvaluationJobWorker {

    public SimpleWorker(DAOFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    protected void evaluate(Submission submission) {
        logger.info("Processing submission: {}", submission.getId());
    }
}
