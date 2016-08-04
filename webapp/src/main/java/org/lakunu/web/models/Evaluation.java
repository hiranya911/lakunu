package org.lakunu.web.models;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.labs.Score;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Evaluation implements Serializable {

    private final String id;
    private final String submissionId;
    private final Date startedAt;
    private final Date finishedAt;
    private final String log;
    private final EvaluationStatus finishingStatus;
    private final ImmutableList<Score> scores;

    private Evaluation(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.id), "ID is required");
        checkArgument(!Strings.isNullOrEmpty(builder.submissionId), "SubmissionID is required");
        checkNotNull(builder.startedAt, "Start time is required");
        checkNotNull(builder.finishedAt, "Finish time is required");
        checkArgument(!builder.startedAt.after(builder.finishedAt), "Timestamp mismatch");
        checkNotNull(builder.log, "Log is required");
        checkNotNull(builder.finishingStatus, "Finish status is required");
        this.id = builder.id;
        this.submissionId = builder.submissionId;
        this.startedAt = builder.startedAt;
        this.finishedAt = builder.finishedAt;
        this.log = builder.log;
        this.finishingStatus = builder.finishingStatus;
        this.scores = ImmutableList.copyOf(builder.scores);
    }

    public String getId() {
        return id;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public String getLog() {
        return log;
    }

    public EvaluationStatus getFinishingStatus() {
        return finishingStatus;
    }

    public ImmutableList<Score> getScores() {
        return scores;
    }

    public double getTotalScore() {
        return scores.stream().mapToDouble(Score::getValue).sum();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String id = "_unidentified_";
        private String submissionId;
        private Date startedAt;
        private Date finishedAt;
        private String log;
        private EvaluationStatus finishingStatus;
        private List<Score> scores = new ArrayList<>();

        private Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setSubmissionId(String submissionId) {
            this.submissionId = submissionId;
            return this;
        }

        public Builder setStartedAt(Date startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public Builder setFinishedAt(Date finishedAt) {
            this.finishedAt = finishedAt;
            return this;
        }

        public Builder setLog(String log) {
            this.log = log;
            return this;
        }

        public Builder setFinishingStatus(EvaluationStatus finishingStatus) {
            this.finishingStatus = finishingStatus;
            return this;
        }

        public Builder addScore(Score score) {
            this.scores.add(score);
            return this;
        }

        public Builder addScores(Collection<Score> scores) {
            this.scores.addAll(scores);
            return this;
        }

        public Evaluation build() {
            return new Evaluation(this);
        }
    }
}
