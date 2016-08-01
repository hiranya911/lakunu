package org.lakunu.web.models;

import com.google.common.base.Strings;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class EvaluationRecord implements Serializable {

    private final String id;
    private final Lab lab;

    public EvaluationRecord(String id, Lab lab) {
        checkArgument(!Strings.isNullOrEmpty(id), "ID is required");
        checkNotNull(lab, "Lab is required");
        this.id = id;
        this.lab = lab;
    }

    public String getId() {
        return id;
    }

    public Lab getLab() {
        return lab;
    }
}
