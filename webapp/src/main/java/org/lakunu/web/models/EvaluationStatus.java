package org.lakunu.web.models;

import java.io.Serializable;

public enum EvaluationStatus implements Serializable {
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
