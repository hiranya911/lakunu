package org.lakunu.labs.plugins.validators;

import org.lakunu.labs.Score;
import org.lakunu.labs.plugins.Plugin;

public final class PluginSuccessValidator extends Validator {

    public PluginSuccessValidator(String name, double score) {
        super(name, score);
    }

    @Override
    public Score validate(Plugin.Context context) {
        return reportScore(context.isSuccess());
    }
}
