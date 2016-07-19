package org.lakunu.labs.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ScoringTask extends Task implements TaskContainer {

    private final List<ValidationTask.PreValidationTask> preTasks = new ArrayList<>();
    private final List<ValidationTask.PostValidationTask> postTasks = new ArrayList<>();
    private final List<Task> tasks = new ArrayList<>();

    public void add(ValidationTask.PreValidationTask pre) {
        preTasks.add(pre);
    }

    public void add(ValidationTask.PostValidationTask post) {
        postTasks.add(post);
    }

    @Override
    public void addTask(Task task) {
        checkNotNull(task, "Task must not be null");
        tasks.add(task);
    }

    @Override
    public void execute() throws BuildException {
        Project project = getProject();
        TaskContext context = new TaskContext(getProject());
        preTasks.stream().forEach(t -> t.execute(context));
        AntOutputListener listener = new AntOutputListener();
        project.addBuildListener(listener);
        try {
            tasks.stream().forEach(Task::perform);
            context.setSuccess(true);
        } catch (Exception e) {
            context.setSuccess(false);
            throw e;
        } finally {
            project.removeBuildListener(listener);
            context.setOutput(listener.getOutput());
            postTasks.stream().forEach(t -> t.execute(context));
        }
    }

}
