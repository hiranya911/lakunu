package org.lakunu.web.dao;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Submission;
import org.lakunu.web.models.SubmissionView;

public interface SubmissionDAO {

    String addSubmission(Submission submission);
    ImmutableList<SubmissionView> getOwnedSubmissions(Lab lab, int limit);
    ImmutableList<SubmissionView> getSubmissionsByUser(Lab lab, String userId, int limit);
    ImmutableList<SubmissionView> getAllSubmissions(Lab lab);
    Submission getSubmission(String submissionId);

}
