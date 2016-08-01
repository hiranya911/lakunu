package org.lakunu.web.queue;

import java.util.Collection;

public interface EvaluationJobQueue {

    String JOB_QUEUE = "JOB_QUEUE";

    void enqueue(String submissionId);
    void enqueue(Collection<String> submissionIds);

}
