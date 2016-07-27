package org.lakunu.web.dao;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Lab;

public interface LabDAO {

    String addLab(Lab lab);
    Lab getLab(String courseId, String labId);
    ImmutableList<Lab> getLabs(String courseId);
    void updateLab(Lab lab);

}
