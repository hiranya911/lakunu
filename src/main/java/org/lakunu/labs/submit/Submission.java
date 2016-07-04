package org.lakunu.labs.submit;

import com.google.common.collect.ImmutableList;
import org.lakunu.labs.EvaluationContext;
import org.lakunu.labs.Lab;
import org.lakunu.labs.plugins.Plugin;
import org.lakunu.labs.utils.LabUtils;
import org.lakunu.labs.utils.LoggingOutputHandler;

import java.io.File;

public abstract class Submission {

    /**
     * Create and initialize the submission directory for the given EvaluationContext.
     * Returns a File representing the initialized submission directory.
     */
    public abstract File initSubmissionDirectory(EvaluationContext context);

    public final void evaluate(Lab lab, String finalPhase) {
        EvaluationContext context = EvaluationContext.newBuilder()
                .setSubmission(this)
                .setWorkingDirectory(lab.getWorkingDirectory())
                .setOutputHandler(new LoggingOutputHandler())
                .build();
        try {
            for (String phase : lab.getPhases()) {
                boolean proceed = runPhase(phase, lab, context);
                if (!proceed || phase.equals(finalPhase)) {
                    break;
                }
            }
        } finally {
            context.cleanup();
        }
    }

    public void evaluate(Lab lab) {
        evaluate(lab, null);
    }

    private boolean runPhase(String phase, Lab lab, EvaluationContext context) {
        ImmutableList<Plugin> pluginList = lab.getPlugins(phase);
        if (pluginList.isEmpty()) {
            return true;
        }

        LabUtils.outputTitle("Starting " + phase + " phase", context.getOutputHandler());
        for (Plugin plugin : pluginList) {
            if (!plugin.execute(context)) {
                return false;
            }
        }
        return true;
    }

}
