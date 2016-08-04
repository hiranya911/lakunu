package org.lakunu.web.dao;

import org.lakunu.web.models.Evaluation;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Submission;

public interface EvaluationDAO {

    Lab getLabForEvaluation(Submission submission);
    boolean addEvaluation(Evaluation evaluation, Lab lab);

}
