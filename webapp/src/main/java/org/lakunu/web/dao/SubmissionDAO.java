package org.lakunu.web.dao;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Submission;
import org.lakunu.web.queue.EvaluationJobQueue;

public interface SubmissionDAO {

    String addSubmission(Submission submission, EvaluationJobQueue jobQueue);
    ImmutableList<Submission> getOwnedSubmissions(String courseId, String labId);

}
