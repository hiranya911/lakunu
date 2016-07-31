package org.lakunu.web.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Submission;
import org.lakunu.web.utils.Security;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lakunu.web.utils.Security.checkPermissions;

public final class SubmissionService extends AbstractDomainService {

    public SubmissionService(DAOFactory daoFactory) {
        super(daoFactory);
    }

    public String addSubmission(Lab lab, String type, byte[] data) {
        checkNotNull(lab, "Lab is required");
        checkPermissions(LabService.SUBMIT_PERMISSION(lab));
        Submission submission = Submission.newBuilder()
                .setLabId(lab.getId())
                .setUserId(Security.getCurrentUser())
                .setSubmittedAt(new Date())
                .setType(type)
                .setData(data)
                .build();
        return daoFactory.getSubmissionDAO().addAndEnqueueSubmission(submission);
    }

    public ImmutableList<Submission> getOwnedSubmissions(String courseId, String labId) {
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        checkArgument(!Strings.isNullOrEmpty(labId), "LabID is required");
        checkPermissions("submission:getOwned:" + courseId + ":" + labId);
        return daoFactory.getSubmissionDAO().getOwnedSubmissions(courseId, labId);
    }
}
