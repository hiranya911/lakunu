package org.lakunu.web.queue;

import org.lakunu.web.service.EvaluationJobWorker;

import java.util.Collection;

public interface EvaluationJobQueue {

    String JOB_QUEUE = "JOB_QUEUE";

    void enqueue(String submissionId);
    void enqueue(Collection<String> submissionIds);
    void addWorker(EvaluationJobWorker worker);
    void cleanup();

}
