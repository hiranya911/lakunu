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

    public Lab addLab(String name, String description, String courseId) {
        checkArgument(!Strings.isNullOrEmpty(name), "name is required");
        checkArgument(name.length() <= 128, "name is too long");
        checkArgument(!Strings.isNullOrEmpty(description), "description is required");
        checkArgument(description.length() <= 512, "description is too long");
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        checkPermissions(ADD_PERMISSION(courseId));

        Lab lab = new Lab();
        lab.setName(name);
        lab.setDescription(description);
        lab.setCourseId(courseId);
        lab.setCreatedBy(Security.getCurrentUser());
        lab.setCreatedAt(new Date());
        String labId = daoFactory.getLabDAO().addLab(lab);
        lab.setId(labId);
        return lab;
    }

    public Lab getLab(String courseId, String labId) {
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        checkArgument(!Strings.isNullOrEmpty(labId), "LabID is required");
        checkPermissions(GET_PERMISSION(courseId, labId));
        return daoFactory.getLabDAO().getLab(labId);
    }

    public boolean updateLab(Update update) {
        // TODO: Return a new Lab instance here (Maintain immutability)
        checkNotNull(update, "Update is required");
        checkPermissions(UPDATE_PERMISSION(update.lab.getCourseId(), update.lab.getId()));
        Lab lab = update.apply();
        return daoFactory.getLabDAO().updateLab(lab);
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

    public static String UPDATE_PERMISSION(String courseId, String labId) {
        return permission("update", courseId, labId);
    }

    public static String UPDATE_PERMISSION(Lab lab) {
        return permission("update", lab.getCourseId(), lab.getId());
    }

    public static Update newUpdate(Lab lab) {
        return new Update(lab);
    }

    public static class Update {

        private final Lab lab;
        private String name;
        private String description;
        private byte[] configuration;

        private Update(Lab lab) {
            checkNotNull(lab, "Lab is required");
            this.lab = lab;
            this.name = lab.getName();
            this.description = lab.getDescription();
            this.configuration = lab.getConfiguration();
        }

        public Update setName(String name) {
            this.name = name;
            return this;
        }

        public Update setDescription(String description) {
            this.description = description;
            return this;
        }

        public Update setConfiguration(byte[] configuration) {
            this.configuration = configuration;
            return this;
        }

        private Lab apply() {
            checkArgument(!Strings.isNullOrEmpty(name), "name is required");
            checkArgument(name.length() <= 128, "name is too long");
            checkArgument(!Strings.isNullOrEmpty(description), "description is required");
            checkArgument(description.length() <= 512, "description is too long");
            lab.setName(name);
            lab.setDescription(description);
            lab.setConfiguration(configuration);
            return lab;
        }
    }
}
