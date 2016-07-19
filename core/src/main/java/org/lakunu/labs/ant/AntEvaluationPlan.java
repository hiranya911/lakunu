package org.lakunu.labs.ant;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.tools.ant.*;
import org.lakunu.labs.Evaluation;
import org.lakunu.labs.EvaluationPlan;
import org.lakunu.labs.Score;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class AntEvaluationPlan implements EvaluationPlan {

    private final File buildFile;
    private final String buildTarget;

    private final ImmutableList<Score> rubric;
    private final ImmutableList<String> phases;

    public AntEvaluationPlan(File buildFile, String buildTarget) {
        checkNotNull(buildFile, "buildFile is required");
        checkArgument(buildFile.exists() && buildFile.isFile(),
                "Build file does not exist or is not a regular file");
        this.buildFile = buildFile;
        this.buildTarget = buildTarget;

        Project project = new Project();
        prepare(project);

        ImmutableList.Builder<Score> rubricBuilder = ImmutableList.builder();
        ImmutableList.Builder<String> phaseBuilder = ImmutableList.builder();
        String targetToExecute = Strings.isNullOrEmpty(buildTarget) ?
                project.getDefaultTarget() : buildTarget;
        Vector<Target> targets = project.topoSort(targetToExecute, project.getTargets(), false);
        targets.forEach(target -> {
            phaseBuilder.add(target.getName());
            for (Task task : target.getTasks()) {
                if (task instanceof UnknownElement && task.getTaskName().equals("score")) {
                    task.maybeConfigure();
                    GradingTask gradingTask = (GradingTask) ((UnknownElement) task).getRealThing();
                    rubricBuilder.addAll(gradingTask.getRubric());
                } else if (task instanceof GradingTask) {
                    rubricBuilder.addAll(((GradingTask) task).getRubric());
                }
            }
        });
        this.rubric = rubricBuilder.build();
        this.phases = phaseBuilder.build();
    }

    private void prepare(Project project) {
        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
        ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
        project.addReference("ant.projectHelper", projectHelper);
        projectHelper.parse(project, buildFile);

        project.init();
        project.addDataTypeDefinition("score", GradingTask.class);
        project.addDataTypeDefinition("pre", ValidationTask.PreValidationTask.class);
        project.addDataTypeDefinition("post", ValidationTask.PostValidationTask.class);
        project.addDataTypeDefinition("arg", ValidatorArg.class);
    }

    @Override
    public void evaluate(Evaluation.Context context, String finalPhase) {
        Project project = new EvaluationProject(context);
        prepare(project);
        try {
            project.fireBuildStarted();
            project.executeTarget(getBuildTarget(project));
            project.fireBuildFinished(null);
        } catch (BuildException ex) {
            project.fireBuildFinished(ex);
            throw new RuntimeException(ex);
        }
    }

    private String getBuildTarget(Project project) {
        // If no target specified then default target will be executed.
        String target = Strings.isNullOrEmpty(buildTarget) ? project.getDefaultTarget() : buildTarget;
        checkArgument(!Strings.isNullOrEmpty(target), "No target specified");
        return target;
    }

    @Override
    public ImmutableList<String> getPhases() {
        return phases;
    }

    @Override
    public ImmutableList<Score> getRubric() {
        return rubric;
    }

    private void addToClassPath() throws Exception {
        File f = new File("/Users/hiranya/Software/Installed/apache-ant-1.9.7/lib");
        File[] children = f.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            if (!child.getName().endsWith(".jar") || child.getName().equals("ant.jar")) {
                continue;
            }
            URI u = child.toURI();
            URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<URLClassLoader> urlClass = URLClassLoader.class;
            Method method = urlClass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(urlClassLoader, u.toURL());
        }
    }

    public static final class EvaluationProject extends Project {

        private final Evaluation.Context context;

        private EvaluationProject(Evaluation.Context context) {
            super();
            this.context = context;
            File resourcesDirectory = context.getResourcesDirectory();
            if (resourcesDirectory != null) {
                this.setUserProperty("resource.dir", resourcesDirectory.getAbsolutePath());
            }
            this.addBuildListener(new AntLabOutputHandler(context.getOutputHandler(), Project.MSG_INFO));
            this.addBuildListener(new AntLabOutputHandler(context.getOutputHandler(), Project.MSG_ERR));
        }

        public Evaluation.Context getContext() {
            return context;
        }
    }
}
