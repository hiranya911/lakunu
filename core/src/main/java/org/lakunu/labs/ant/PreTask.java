package org.lakunu.labs.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.util.ArrayList;
import java.util.List;


public class PreTask {

    private String type;
    private final List<ValidatorArg> args = new ArrayList<>();

    public void setType(String type) {
        this.type = type;
    }

    public void add(ValidatorArg arg) {
        this.args.add(arg);
    }

    public void execute(Project project) throws BuildException {
        System.out.println("IN PRE for: " + project.getName() + ": " + type);
        project.setUserProperty("lakunu:pre", "5");
    }
}
