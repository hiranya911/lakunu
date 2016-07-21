package org.lakunu.labs.ant;

import com.google.common.base.Strings;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Reference;

import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class LabResource extends FileSet {

    private File getResource(String name) {
        checkState(!Strings.isNullOrEmpty(name), "name is required");
        checkState(getProject() instanceof AntEvaluationPlan.EvaluationProject, "invalid project type");
        AntEvaluationPlan.EvaluationProject project = (AntEvaluationPlan.EvaluationProject) getProject();
        return project.getContext().lookupResource(name);
    }

    public synchronized void setName(String name) {
        File file = getResource(name);
        checkNotNull(file, "No resource found by the name: %s", name);
        FileSet fileSet = new FileSet();
        fileSet.setProject(getProject());
        if (file.isDirectory()) {
            this.setDir(file);
        } else {
            this.setFile(file);
        }
    }

    @Override
    public void setRefid(Reference r) {
        throw new UnsupportedOperationException("refId not supported");
    }

}
