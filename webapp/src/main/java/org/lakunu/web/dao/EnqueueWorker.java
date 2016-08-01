package org.lakunu.web.dao;

import org.lakunu.web.queue.EvaluationJobQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class EnqueueWorker implements Runnable {

    public static final String ENQUEUE_WORKER = "ENQUEUE_WORKER";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final ExecutorService executor;
    private final Future<?> future;
    private final EvaluationJobQueue jobQueue;

    private boolean proceed;

    public EnqueueWorker(EvaluationJobQueue jobQueue) {
        checkNotNull(jobQueue, "JobQueue is required");
        this.executor = Executors.newSingleThreadExecutor();
        this.jobQueue = jobQueue;
        this.proceed = true;
        this.future = this.executor.submit(this);
    }

    @Override
    public final void run() {
        logger.info("Initializing enqueue worker");
        while (proceed) {
            int count = enqueue(jobQueue);
            if (count == 0) {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public final void cleanup() {
        this.proceed = false;
        this.future.cancel(true);
        this.executor.shutdownNow();
        logger.info("Enqueue worker terminated");
    }

    protected abstract int enqueue(EvaluationJobQueue jobQueue);
}
