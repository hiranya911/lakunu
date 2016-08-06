package org.lakunu.web.service;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Submission;
import org.lakunu.web.models.SubmissionView;
import org.lakunu.web.service.submissions.UserSubmission;
import org.lakunu.web.utils.Security;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Date submittedAt = new Date();
        checkArgument(lab.isOpenForSubmissions(submittedAt), "Lab is not open for submissions");
        Submission submission = Submission.newBuilder()
                .setLabId(lab.getId())
                .setUserId(Security.getCurrentUser())
                .setSubmittedAt(submittedAt)
                .setType(userSubmission.getType())
                .setData(userSubmission.getData())
                .build();
        return daoFactory.getSubmissionDAO().addSubmission(submission);
    }

    public ImmutableList<SubmissionView> getOwnedSubmissions(Lab lab, int limit) {
        checkNotNull(lab, "Lab is required");
        checkPermissions("submission:getOwned:" + lab.getCourseId() + ":" + lab.getId());
        return daoFactory.getSubmissionDAO().getOwnedSubmissions(lab, limit);
    }

    public Map<String,List<SubmissionView>> getAllSubmissions(Lab lab) {
        checkNotNull(lab, "Lab is required");
        checkPermissions("submission:getAll:" + lab.getCourseId() + ":" + lab.getId());
        ImmutableList<SubmissionView> submissions = daoFactory.getSubmissionDAO()
                .getAllSubmissions(lab);
        return submissions.stream().collect(Collectors.groupingBy(SubmissionView::getUserId));
    }
}
