package org.lakunu.web.dao;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Submission;
import org.lakunu.web.models.SubmissionView;
import org.lakunu.web.queue.EvaluationJobQueue;

public interface SubmissionDAO {

    String addSubmission(Submission submission);
    ImmutableList<SubmissionView> getOwnedSubmissions(String courseId, String labId, int limit);
    Submission getSubmission(String submissionId);

}
