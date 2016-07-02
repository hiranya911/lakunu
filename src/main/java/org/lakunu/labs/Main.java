package org.lakunu.labs;

import org.lakunu.labs.build.AntBuildPlugin;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        Lifecycle lifecycle = DefaultLifecycle.newBuilder()
                .addPlugin("build", new AntBuildPlugin("ant", "compile"))
                .build();
        File file = new File("/Users/hiranya/academic/cs56/github-grader/target/source/lab00_EdieS");
        lifecycle.run(file);
    }

}
