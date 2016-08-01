package org.lakunu.web.api;

import org.lakunu.web.queue.EvaluationJobQueue;
import org.lakunu.web.service.CourseService;
import org.lakunu.web.service.DAOFactory;
import org.lakunu.web.service.SubmissionService;
import org.lakunu.web.service.LabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public abstract class LakunuController extends HttpServlet {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected CourseService courseService;
    protected LabService labService;
    protected SubmissionService submissionService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        DAOFactory daoFactory = (DAOFactory) config.getServletContext().getAttribute(
                DAOFactory.DAO_FACTORY);
        EvaluationJobQueue jobQueue = (EvaluationJobQueue) config.getServletContext().getAttribute(
                EvaluationJobQueue.JOB_QUEUE) ;
        this.courseService = new CourseService(daoFactory);
        this.labService = new LabService(daoFactory);
        this.submissionService = new SubmissionService(daoFactory, jobQueue);
    }
}
