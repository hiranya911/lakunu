package org.lakunu.web.queue;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.service.EvaluationJobWorker;

import java.util.Collection;

public interface EvaluationJobQueue {

    String JOB_QUEUE = "JOB_QUEUE";

    void enqueue(Collection<String> submissionIds);
    void addWorker(EvaluationJobWorker worker);
    ImmutableList<String> getPendingSubmissions();
    void cleanup();

}
