package org.lakunu.web.dao;

import org.lakunu.web.models.Evaluation;
import org.lakunu.web.models.EvaluationRecord;

public interface EvaluationDAO {

    EvaluationRecord startEvaluation(Evaluation evaluation);

}
