package org.lakunu.web.dao;

import org.lakunu.web.models.Lab;

public interface LabDAO {

    String addLab(Lab lab);
    Lab getLab(String labId);
    boolean updateLab(Lab lab);

}
