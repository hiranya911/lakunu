package org.lakunu.web.service;

import org.lakunu.web.dao.CourseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DAOFactory {

    public static final String DAO_FACTORY = "DAO_FACTORY";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract CourseDAO getCourseDAO();

}
