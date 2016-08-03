package org.lakunu.web.dao;

import org.lakunu.web.models.EvaluationRecord;

import java.util.Date;

public interface EvaluationDAO {

    EvaluationRecord startEvaluation(String submissionId, Date startedAt);

    void finishEvaluation(EvaluationRecord record);

}
