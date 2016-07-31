package org.lakunu.web.dao;

public interface EvaluationJobQueue {

    void enqueue(String submissionId);

}
