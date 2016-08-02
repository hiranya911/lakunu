package org.lakunu.web.workers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.lakunu.labs.Score;

import static com.google.common.base.Preconditions.checkNotNull;

public final class LabOutputParser {

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
            if ("Evaluation complete".equals(line)) {
                summary = true;
            } else if (summary && line.startsWith("Score:")) {
                String[] segments = StringUtils.split(line);
                if (segments.length == 5) {
                    scores.add(Score.newPoints(segments[1], Double.parseDouble(segments[2]),
                            Double.parseDouble(segments[4])));
                } else if (segments.length == 3) {
                    scores.add(Score.newPenalty(segments[1], Double.parseDouble(segments[2])));
                }
            }
        }
        return scores.build();
    }

}
