package org.lakunu.web.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lakunu.web.utils.Security.checkPermissions;

public abstract class LabDAO {

    public final String addLab(Lab lab) {
        checkNotNull(lab, "Lab is required");
        checkPermissions("course:addLab:" + lab.getCourseId());
        try {
            return doAddLab(lab);
        } catch (Exception e) {
            throw new DAOException("Error while creating lab", e);
        }
    }

    protected abstract String doAddLab(Lab lab) throws Exception;

}
