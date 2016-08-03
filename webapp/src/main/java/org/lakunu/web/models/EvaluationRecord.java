package org.lakunu.web.models;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.labs.Score;

import java.io.Serializable;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class EvaluationRecord implements Serializable {

    private final String id;
    private final Lab lab;
    private final Date finishedAt;
    private final int finishingStatus;
    private final ImmutableList<Score> scores;
    private final String log;

    public EvaluationRecord(String id, Lab lab) {
        checkArgument(!Strings.isNullOrEmpty(id), "ID is required");
        checkNotNull(lab, "Lab is required");
        this.id = id;
        this.lab = lab;
        this.finishedAt = null;
        this.finishingStatus = -1;
        this.scores = null;
        this.log = null;
    }

    private EvaluationRecord(Completion finish) {
        checkNotNull(finish.finishedAt, "Finished at is required");
        checkArgument(finish.finishingStatus >= 0, "Finish status must be positive");
        checkNotNull(finish.scores, "Scores list is required");
        checkNotNull(finish.log, "Log is required");
        this.id = finish.original.id;
        this.lab = finish.original.lab;
        this.finishedAt = finish.finishedAt;
        this.finishingStatus = finish.finishingStatus;
        this.scores = finish.scores;
        this.log = finish.log;
    }

    public String getId() {
        return id;
    }

    public Lab getLab() {
        return lab;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public int getFinishingStatus() {
        return finishingStatus;
    }

    public ImmutableList<Score> getScores() {
        return scores;
    }

    public String getLog() {
        return log;
    }

    public Completion finishEvaluation() {
        return new Completion(this);
    }

    public static class Completion extends Mutation<EvaluationRecord> {

        private Date finishedAt;
        private int finishingStatus;
        private ImmutableList<Score> scores;
        private String log;

        private Completion(EvaluationRecord original) {
            super(original);
        }

        public Completion setFinishedAt(Date finishedAt) {
            this.finishedAt = finishedAt;
            return this;
        }

        public Completion setFinishingStatus(int finishingStatus) {
            this.finishingStatus = finishingStatus;
            return this;
        }

        public Completion setScores(ImmutableList<Score> scores) {
            this.scores = scores;
            return this;
        }

        public Completion setLog(String log) {
            this.log = log;
            return this;
        }

        @Override
        public EvaluationRecord apply() {
            return new EvaluationRecord(this);
        }
    }
}
