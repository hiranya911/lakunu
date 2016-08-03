package org.lakunu.web.models;

public enum EvaluationStatus {
    SUCCESS(1),
    FAILED(2);

    private final int status;

    EvaluationStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
