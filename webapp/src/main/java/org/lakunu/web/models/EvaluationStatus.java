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

    public static EvaluationStatus fromInt(int i) {
        switch (i) {
            case 1:
                return SUCCESS;
            case 2:
                return FAILED;
        }
        throw new IllegalArgumentException("Invalid status: " + i);
    }
}
