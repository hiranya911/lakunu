package org.lakunu.web.data;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lakunu.web.utils.Security.checkPermissions;

public abstract class LabDAO {

    public final String addLab(Lab lab) {
        checkNotNull(lab, "Lab is required");
        checkPermissions(ADD_PERMISSION(lab.getCourseId()));
        try {
            return doAddLab(lab);
        } catch (Exception e) {
            throw new DAOException("Error while creating lab", e);
        }
    }

    public final Lab getLab(String courseId, String labId) {
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        checkArgument(!Strings.isNullOrEmpty(labId), "LabID is required");
        checkPermissions(GET_PERMISSION(courseId, labId));
        try {
            return doGetLab(courseId, labId);
        } catch (Exception e) {
            throw new DAOException("Error while retrieving lab", e);
        }
    }

    public final void updateLab(Lab lab) {
        checkNotNull(lab, "Lab is required");
        checkPermissions(UPDATE_PERMISSION(lab));
        try {
            doUpdateLab(lab);
        } catch (Exception e) {
            throw new DAOException("Error while updating lab", e);
        }
    }

    public final void publishLab(Lab lab) {
        checkNotNull(lab, "Lab is required");
        checkPermissions(PUBLISH_PERMISSION(lab));
        try {
            doPublishLab(lab);
        } catch (Exception e) {
            throw new DAOException("Error while publishing lab", e);
        }
    }

    protected abstract String doAddLab(Lab lab) throws Exception;
    protected abstract Lab doGetLab(String courseId, String labId) throws Exception;
    protected abstract void doUpdateLab(Lab lab) throws Exception;
    protected abstract void doPublishLab(Lab lab) throws Exception;

    public static String ADD_PERMISSION(String courseId) {
        return Lab.permission("add", courseId, "*");
    }

    public static String GET_PERMISSION(String courseId, String labId) {
        return Lab.permission("get", courseId, labId);
    }

    public static String UPDATE_PERMISSION(Lab lab) {
        return Lab.permission("update", lab);
    }

    public static String PUBLISH_PERMISSION(Lab lab) {
        return Lab.permission("publish", lab);
    }
}
