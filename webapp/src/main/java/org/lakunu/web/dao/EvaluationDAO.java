package org.lakunu.web.dao;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Submission;

public interface EvaluationDAO {

    ImmutableList<Submission> getOwnedSubmissions(String courseId, String labId);

}
