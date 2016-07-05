package org.lakunu.labs;

import static com.google.common.base.Preconditions.checkArgument;

public final class Score {

    private final int score;
    private final int maxScore;

    public Score(int score, int maxScore) {
        checkArgument(score <= maxScore, "score must not exceed maxScore");
        checkArgument(maxScore >= 0, "maxScore must not be negative");
        this.score = score;
        this.maxScore = maxScore;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public Score add(Score score) {
        return new Score(this.score + score.score, this.maxScore + score.maxScore);
    }
}
