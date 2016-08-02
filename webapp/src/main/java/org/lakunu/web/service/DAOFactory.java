package org.lakunu.web.service;

import org.lakunu.web.dao.CourseDAO;
import org.lakunu.web.dao.EvaluationDAO;
import org.lakunu.web.dao.SubmissionDAO;
import org.lakunu.web.dao.LabDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DAOFactory {

    public static final String DAO_FACTORY = "DAO_FACTORY";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract CourseDAO getCourseDAO();

    protected abstract LabDAO getLabDAO();

    protected abstract SubmissionDAO getSubmissionDAO();

    protected abstract EvaluationDAO getEvaluationDAO();

    public abstract void cleanup();
}
