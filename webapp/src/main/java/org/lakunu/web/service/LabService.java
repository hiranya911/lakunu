package org.lakunu.web.service;

import com.google.common.base.Strings;
import org.lakunu.web.models.Lab;
import org.lakunu.web.utils.Security;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lakunu.web.utils.Security.checkPermissions;

public final class LabService extends AbstractDomainService {

    public LabService(DAOFactory daoFactory) {
        super(daoFactory);
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

}
