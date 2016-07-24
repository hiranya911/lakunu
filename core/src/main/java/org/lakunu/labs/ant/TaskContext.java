package org.lakunu.labs.ant;

import org.lakunu.labs.Score;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public final class TaskContext {

    private final AntEvaluationPlan.EvaluationProject project;
    private final Map<String,Object> properties = new HashMap<>();

    private boolean success;
    private String output;
    private String error;

    public TaskContext(AntEvaluationPlan.EvaluationProject project) {
        checkNotNull(project, "Project is required");
        this.project = project;
    }

    public AntEvaluationPlan.EvaluationProject getProject() {
        return project;
    }

    public void addScore(Score score) {
        checkNotNull(score, "Score cannot be null");
        project.getContext().addScore(score);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    public <T> T getProperty(String name, Class<T> clazz) {
        return clazz.cast(properties.get(name));
    }
}
