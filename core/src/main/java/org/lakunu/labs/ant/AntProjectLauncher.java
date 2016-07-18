package org.lakunu.labs.ant;

import com.google.common.base.Strings;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;

import static com.google.common.base.Preconditions.checkArgument;

public final class AntProjectLauncher {

    private final File buildFile;
    private final String target;

    public AntProjectLauncher(String buildFilePath, String target) {
        checkArgument(!Strings.isNullOrEmpty(buildFilePath), "buildFilePath is required");
        File buildFile = new File(buildFilePath);
        checkArgument(buildFile.exists() && buildFile.isFile(),
                "Build file does not exist or is not a regular file");
        this.buildFile = buildFile;
        this.target = target;
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

    public void run() {
        try {
            addToClassPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        DefaultLogger consoleLogger = getConsoleLogger();

        // Prepare Ant project
        Project project = new Project();
        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
        project.addBuildListener(consoleLogger);

        // Capture event for Ant script build start / stop / failure
        try {
            project.fireBuildStarted();
            project.init();
            project.addDataTypeDefinition("pre", PreTask.class);
            project.addDataTypeDefinition("post", PostTask.class);
            project.addDataTypeDefinition("arg", ValidatorArg.class);
            ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
            project.addReference("ant.projectHelper", projectHelper);
            projectHelper.parse(project, buildFile);

            // If no target specified then default target will be executed.
            String targetToExecute = Strings.isNullOrEmpty(target) ? project.getDefaultTarget() : target;
            project.executeTarget(targetToExecute);
            project.fireBuildFinished(null);
        } catch (BuildException ex) {
            project.fireBuildFinished(ex);
            throw new RuntimeException("!!! Unable to restart the IEHS App !!!", ex);
        } finally {
            Hashtable<String, Object> userProperties = project.getUserProperties();
            userProperties.keySet().stream().forEach(k -> {
                if (k.startsWith("lakunu:")) {
                    System.out.println("Score: " + userProperties.get(k));
                }
            });
        }
    }

    private static DefaultLogger getConsoleLogger() {
        DefaultLogger consoleLogger = new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
        return consoleLogger;
    }

    public static void main(String[] args) throws Exception {
        AntProjectLauncher launcher = new AntProjectLauncher(
                "/Users/hiranya/academic/cs56/github-grader/target/source/lab00_EdieS/build.xml",
                "test");
        launcher.run();
    }
}
