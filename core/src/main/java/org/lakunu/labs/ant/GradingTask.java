package org.lakunu.labs.ant;

import com.google.common.collect.ImmutableList;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.lakunu.labs.EvaluationPlan;
import org.lakunu.labs.Score;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class GradingTask extends Task implements TaskContainer {

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

    public ImmutableList<Score> getRubric() {
        ImmutableList.Builder<Score> rubric = ImmutableList.builder();
        preTasks.forEach(t -> rubric.add(t.getRubric()));
        postTasks.forEach(t -> rubric.add(t.getRubric()));
        return rubric.build();
    }

    @Override
    public void execute() throws BuildException {
        AntEvaluationPlan.EvaluationProject project = (AntEvaluationPlan.EvaluationProject) getProject();
        TaskContext context = new TaskContext(project);
        preTasks.stream().forEach(t -> t.execute(context));

        TaskOutputRecorder outputRecorder = new TaskOutputRecorder();
        project.addBuildListener(outputRecorder);
        try {
            tasks.stream().forEach(Task::perform);
            context.setSuccess(true);
        } catch (Exception e) {
            context.setSuccess(false);
            throw e;
        } finally {
            project.removeBuildListener(outputRecorder);
            context.setOutput(outputRecorder.getOutput());
            postTasks.stream().forEach(t -> t.execute(context));
        }
    }

}
