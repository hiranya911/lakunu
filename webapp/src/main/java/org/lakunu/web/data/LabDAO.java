package org.lakunu.web.data;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lakunu.web.utils.Security.checkPermissions;

public abstract class LabDAO {

    public final String addLab(Lab lab) {
        checkNotNull(lab, "Lab is required");
        checkPermissions("lab:add:" + lab.getCourseId());
        try {
            return doAddLab(lab);
        } catch (Exception e) {
            throw new DAOException("Error while creating lab", e);
        }
    }

    public final Lab getLab(String courseId, String labId) {
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        checkArgument(!Strings.isNullOrEmpty(labId), "LabID is required");
        checkPermissions("lab:get:" + courseId + ":" + labId);
        try {
            return doGetLab(courseId, labId);
        } catch (Exception e) {
            throw new DAOException("Error while retrieving lab", e);
        }
    }

    public final void updateLab(Lab lab) {
        checkNotNull(lab, "Lab is required");
        checkPermissions("lab:update:" + lab.getCourseId() + ":" + lab.getId());
        try {
            doUpdateLab(lab);
        } catch (Exception e) {
            throw new DAOException("Error while updating lab", e);
        }
    }

    protected abstract String doAddLab(Lab lab) throws Exception;
    protected abstract Lab doGetLab(String courseId, String labId) throws Exception;
    protected abstract void doUpdateLab(Lab lab) throws Exception;

}
