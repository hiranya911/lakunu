package org.lakunu.web.dao;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Submission;

public interface SubmissionDAO {

    String addSubmission(Submission submission);
    ImmutableList<Submission> getOwnedSubmissions(String courseId, String labId);

}
