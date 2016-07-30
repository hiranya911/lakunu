package org.lakunu.web.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Submission;

import static com.google.common.base.Preconditions.checkArgument;
import static org.lakunu.web.utils.Security.checkPermissions;

public final class EvaluationService extends AbstractDomainService {

    public EvaluationService(DAOFactory daoFactory) {
        super(daoFactory);
    }

    public ImmutableList<Submission> getOwnedSubmissions(String courseId, String labId) {
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        checkArgument(!Strings.isNullOrEmpty(labId), "LabID is required");
        checkPermissions("lab:getOwnedSubmissions:" + courseId + ":" + labId);
        return daoFactory.getEvaluationDAO().getOwnedSubmissions(courseId, labId);
    }
}
