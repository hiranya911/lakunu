package org.lakunu.web.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import org.apache.shiro.authz.AuthorizationException;
import org.lakunu.web.dao.SubmissionDAO;
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
import static org.lakunu.web.utils.Security.hasPermission;

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

    public void enqueueSubmission(Lab lab, String submissionId) {
        checkNotNull(lab, "Lab is required");
        checkArgument(!Strings.isNullOrEmpty(submissionId), "SubmissionID is required");
        checkPermissions(LabService.ENQUEUE_SUBMISSION_PERMISSION(lab));
        SubmissionDAO submissionDAO = daoFactory.getSubmissionDAO();
        Submission submission = submissionDAO.getSubmission(submissionId);
        checkNotNull(submission, "Invalid submission ID");
        checkArgument(lab.getId().equals(submission.getLabId()), "Invalid lab for submission");
        submissionDAO.enqueueSubmissions(ImmutableList.of(submission.getId()));
    }

    public ImmutableList<SubmissionView> getOwnedSubmissions(Lab lab, int limit) {
        checkNotNull(lab, "Lab is required");
        checkPermissions(LabService.GET_OWNED_SUBMISSIONS_PERMISSION(lab));
        return daoFactory.getSubmissionDAO().getOwnedSubmissions(lab, limit);
    }

    public Submission getSubmission(Lab lab, String submissionId) {
        checkNotNull(lab, "Lab is required");
        checkArgument(!Strings.isNullOrEmpty(submissionId), "SubmissionID is required");
        Submission submission = daoFactory.getSubmissionDAO().getSubmission(submissionId);
        if (hasPermission(LabService.GET_SUBMISSION_PERMISSION(lab))) {
            return submission;
        } else if (hasPermission(LabService.GET_OWNED_SUBMISSIONS_PERMISSION(lab))) {
            checkArgument(submission.getUserId().equals(Security.getCurrentUser()),
                    "Operation not permitted");
            return submission;
        } else {
            throw new AuthorizationException("Operation not permitted");
        }
    }

    public ImmutableList<SubmissionView> getSubmissionsForGrading(Lab lab, String userId,
                                                                  int limit) {
        checkNotNull(lab, "Lab is required");
        checkArgument(!Strings.isNullOrEmpty(userId), "UserID is required");
        checkPermissions(LabService.GRADE_PERMISSION(lab));
        return daoFactory.getSubmissionDAO().getSubmissionsByUser(lab, userId, limit);
    }

    public Map<String,List<SubmissionView>> getSubmissionsForGrading(Lab lab) {
        checkNotNull(lab, "Lab is required");
        checkPermissions(LabService.GRADE_PERMISSION(lab));
        ImmutableList<SubmissionView> submissions = daoFactory.getSubmissionDAO()
                .getAllSubmissions(lab);
        return submissions.stream().collect(Collectors.groupingBy(SubmissionView::getUserId));
    }

    public ImmutableSortedMap<String,Double> exportGrades(Lab lab, ImmutableList<String> users) {
        checkNotNull(users, "Users list is required");
        Map<String,List<SubmissionView>> submissions = getSubmissionsForGrading(lab);
        ImmutableSortedMap.Builder<String,Double> result = ImmutableSortedMap.naturalOrder();
        users.forEach(user -> {
            List<SubmissionView> userSubmissions = submissions.getOrDefault(user, ImmutableList.of());
            if (userSubmissions.isEmpty()) {
                result.put(user, 0D);
            } else {
                result.put(user, userSubmissions.get(0).getFinalScore().getValue());
            }
        });
        return result.build();
    }
}
