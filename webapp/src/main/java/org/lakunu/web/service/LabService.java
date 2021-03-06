package org.lakunu.web.service;

import com.google.common.base.Strings;
import org.lakunu.web.ant.AntEvaluationBridge;
import org.lakunu.web.models.Lab;
import org.lakunu.web.utils.Security;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lakunu.web.utils.Security.checkPermissions;

public final class LabService extends AbstractDomainService {

    private final EvaluationBridge evaluationBridge;

    public LabService(DAOFactory daoFactory) {
        super(daoFactory);
        // TODO: Make this configurable
        this.evaluationBridge = new AntEvaluationBridge();
    }

    public String addLab(String name, String description, String courseId) {
        checkPermissions(ADD_PERMISSION(courseId));
        Lab lab = Lab.newBuilder()
                .setName(name)
                .setDescription(description)
                .setCourseId(courseId)
                .setCreatedBy(Security.getCurrentUser())
                .setCreatedAt(new Date())
                .build();
        return daoFactory.getLabDAO().addLab(lab);
    }

    public Lab getLab(String courseId, String labId) {
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        checkArgument(!Strings.isNullOrEmpty(labId), "LabID is required");
        checkPermissions(GET_PERMISSION(courseId, labId));
        return daoFactory.getLabDAO().getLab(courseId, labId);
    }

    public Lab updateLab(Lab.Update update) {
        checkNotNull(update, "Update is required");
        Lab lab = update.apply();
        checkPermissions(UPDATE_PERMISSION(lab));
        daoFactory.getLabDAO().updateLab(lab);
        return lab;
    }

    public Lab publishLab(Lab.PublishSettings publishSettings) {
        checkNotNull(publishSettings, "PublishSettings are required");
        Lab lab = publishSettings.apply();
        checkArgument(lab.isPublished(), "Publish must be true");
        checkPermissions(PUBLISH_PERMISSION(lab));
        Date submissionDeadline = lab.getSubmissionDeadline();
        if (submissionDeadline != null) {
            checkArgument(submissionDeadline.after(new Date()),
                    "Submission deadline must be in the future");
        }

        try {
            evaluationBridge.validate(lab);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        daoFactory.getLabDAO().updateLab(lab);
        return lab;
    }

    public Lab unpublishLab(Lab lab) {
        checkNotNull(lab, "Lab is required");
        checkPermissions(PUBLISH_PERMISSION(lab));
        checkState(lab.isPublished(), "Lab is not in published state");
        lab = lab.newPublishSettings().setPublished(false).setSubmissionDeadline(null).apply();
        checkArgument(!lab.isPublished(), "Publish must be false");
        daoFactory.getLabDAO().updateLab(lab);
        return lab;
    }

    public static String permission(String op, String courseId, String labId) {
        checkArgument(!Strings.isNullOrEmpty(op), "Operation is required");
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        checkArgument(!Strings.isNullOrEmpty(labId), "LabID is required");
        return String.format("lab:%s:%s:%s", op, courseId, labId);
    }

    public static String ADD_PERMISSION(String courseId) {
        return permission("add", courseId, "*");
    }

    public static String GET_PERMISSION(String courseId, String labId) {
        return permission("get", courseId, labId);
    }

    public static String UPDATE_PERMISSION(Lab lab) {
        return permission("update", lab.getCourseId(), lab.getId());
    }

    public static String PUBLISH_PERMISSION(Lab lab) {
        return permission("publish", lab.getCourseId(), lab.getId());
    }

    public static String SUBMIT_PERMISSION(Lab lab) {
        return SUBMIT_PERMISSION(lab.getCourseId(), lab.getId());
    }

    public static String SUBMIT_PERMISSION(String courseId, String labId) {
        return permission("submit", courseId, labId);
    }

    public static String GRADE_PERMISSION(Lab lab) {
        return permission("grade", lab.getCourseId(), lab.getId());
    }

    public static String ENQUEUE_SUBMISSION_PERMISSION(Lab lab) {
        return LabService.permission("enqueueSubmission", lab.getCourseId(), lab.getId());
    }

    public static String GET_OWNED_SUBMISSIONS_PERMISSION(Lab lab) {
        return LabService.permission("getOwnedSubmissions", lab.getCourseId(), lab.getId());
    }

    public static String GET_SUBMISSION_PERMISSION(Lab lab) {
        return LabService.permission("getSubmission", lab.getCourseId(), lab.getId());
    }

}
