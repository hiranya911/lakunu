package org.lakunu.web.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Submission;
import org.lakunu.web.models.SubmissionView;
import org.lakunu.web.service.submissions.UserSubmission;
import org.lakunu.web.utils.Security;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lakunu.web.utils.Security.checkPermissions;

public final class SubmissionService extends AbstractDomainService {

    public SubmissionService(DAOFactory daoFactory) {
        super(daoFactory);
    }

    public String addSubmission(Lab lab, UserSubmission userSubmission) {
        checkNotNull(lab, "Lab is required");
        checkNotNull(userSubmission, "UserSubmission is required");
        checkPermissions(LabService.SUBMIT_PERMISSION(lab));
        Submission submission = Submission.newBuilder()
                .setLabId(lab.getId())
                .setUserId(Security.getCurrentUser())
                .setSubmittedAt(new Date())
                .setType(userSubmission.getType())
                .setData(userSubmission.getData())
                .build();
        return daoFactory.getSubmissionDAO().addSubmission(submission);
    }

    public ImmutableList<SubmissionView> getOwnedSubmissions(String courseId, String labId) {
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        checkArgument(!Strings.isNullOrEmpty(labId), "LabID is required");
        checkPermissions("submission:getOwned:" + courseId + ":" + labId);
        return daoFactory.getSubmissionDAO().getOwnedSubmissions(courseId, labId);
    }
}
