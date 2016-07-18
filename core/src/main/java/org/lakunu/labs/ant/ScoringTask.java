package org.lakunu.labs.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkNotNull;

public class ScoringTask extends Task implements TaskContainer {

    private final List<PreTask> preTasks = new ArrayList<>();
    private final List<PostTask> postTasks = new ArrayList<>();
    private final List<Task> tasks = new ArrayList<>();

    public void add(PreTask pre) {
        preTasks.add(pre);
    }

    public void add(PostTask post) {
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
        preTasks.stream().forEach(t -> t.execute(project));
        AntOutputListener listener = new AntOutputListener();
        project.addBuildListener(listener);
        AtomicBoolean status = new AtomicBoolean(true);
        try {
            tasks.stream().forEach(Task::perform);
        } catch (Exception e) {
            status.set(false);
            throw e;
        } finally {
            project.removeBuildListener(listener);
            postTasks.stream().forEach(t -> t.execute(project, status.get(), listener.getOutput()));
        }
    }

}
