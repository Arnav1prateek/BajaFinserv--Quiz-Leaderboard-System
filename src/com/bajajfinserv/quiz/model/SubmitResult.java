package com.bajajfinserv.quiz.model;

public class SubmitResult {
    private final boolean correct;
    private final boolean idempotent;
    private final int submittedTotal;
    private final int expectedTotal;
    private final String message;

    public SubmitResult(boolean correct, boolean idempotent, int submittedTotal, int expectedTotal, String message) {
        this.correct = correct;
        this.idempotent = idempotent;
        this.submittedTotal = submittedTotal;
        this.expectedTotal = expectedTotal;
        this.message = message;
    }

    public boolean isCorrect() {
        return correct;
    }

    public boolean isIdempotent() {
        return idempotent;
    }

    public int getSubmittedTotal() {
        return submittedTotal;
    }

    public int getExpectedTotal() {
        return expectedTotal;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SubmitResult{" +
                "correct=" + correct +
                ", idempotent=" + idempotent +
                ", submittedTotal=" + submittedTotal +
                ", expectedTotal=" + expectedTotal +
                ", message='" + message + '\'' +
                '}';
    }
}
