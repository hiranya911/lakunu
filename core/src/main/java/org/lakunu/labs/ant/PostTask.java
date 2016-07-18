package org.lakunu.labs.ant;

import org.apache.tools.ant.Project;

public class PostTask {

    public void execute(Project project, boolean status, String output) {
        System.out.println("Running POST: " + output);
    }

}
