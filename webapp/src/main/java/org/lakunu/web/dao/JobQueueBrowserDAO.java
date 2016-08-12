package org.lakunu.web.dao;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.queue.EvaluationJobQueue;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class JobQueueBrowserDAO {

    protected final EvaluationJobQueue jobQueue;

    protected JobQueueBrowserDAO(EvaluationJobQueue jobQueue) {
        checkNotNull(jobQueue, "Job queue is required");
        this.jobQueue = jobQueue;
    }

    public abstract ImmutableList<String> getPendingSubmissions();

}
