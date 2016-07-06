package org.lakunu.labs.plugins.validators;

import com.google.common.base.Strings;
import org.lakunu.labs.Score;
import org.lakunu.labs.plugins.Plugin;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

public final class OutputMatchValidator extends Validator {

    private final Pattern pattern;

    public OutputMatchValidator(String name, double score, String pattern) {
        super(name, score);
        checkArgument(!Strings.isNullOrEmpty(pattern), "Pattern is required");
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public Score validate(Plugin.Context context) {
        String output = context.getOutput();
        return reportScore(output != null && pattern.matcher(output).matches());
    }
}
