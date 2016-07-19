package org.lakunu.labs.ant;

import org.apache.tools.ant.Project;

public final class TaskContext {

    private final Project project;
    private boolean success;
    private String output;

    public TaskContext(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
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
}
