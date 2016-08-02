package org.lakunu.web.workers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.lakunu.labs.Score;

import static com.google.common.base.Preconditions.checkNotNull;

public final class LabOutputParser {

    public static final String END_MARKER = "@@@END_OF_LAB@@@";
    public static final String SCORE_PREFIX = "Score:";
    private final String output;

    public LabOutputParser(String output) {
        checkNotNull(output, "Output is required");
        this.output = output;
    }

    public ImmutableList<Score> getScores() {
        boolean summary = false;
        ImmutableList.Builder<Score> scores = ImmutableList.builder();
        String[] lines = StringUtils.split(output, "\r\n");
        for (String line : lines) {
            line = line.trim();
            if (line.contains(END_MARKER)) {
                summary = true;
            } else if (summary && line.startsWith(SCORE_PREFIX)) {
                scores.add(Score.fromString(line.substring(SCORE_PREFIX.length()).trim()));
            }
        }
        return scores.build();
    }

}
