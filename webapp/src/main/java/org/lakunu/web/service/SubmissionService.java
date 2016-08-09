package org.lakunu.web.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
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
