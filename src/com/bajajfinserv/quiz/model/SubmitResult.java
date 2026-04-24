package com.bajajfinserv.quiz.model;

public class SubmitResult {
    private final Boolean correct;
    private final Boolean idempotent;
    private final Integer submittedTotal;
    private final Integer expectedTotal;
    private final String message;
    private final String regNo;
    private final Integer totalPollsMade;
    private final Integer attemptCount;

    public SubmitResult(Boolean correct, Boolean idempotent, Integer submittedTotal, Integer expectedTotal, String message,
                        String regNo, Integer totalPollsMade, Integer attemptCount) {
        this.correct = correct;
        this.idempotent = idempotent;
        this.submittedTotal = submittedTotal;
        this.expectedTotal = expectedTotal;
        this.message = message;
        this.regNo = regNo;
        this.totalPollsMade = totalPollsMade;
        this.attemptCount = attemptCount;
    }

    public Boolean isCorrect() {
        return correct;
    }

    public Boolean isIdempotent() {
        return idempotent;
    }

    public Integer getSubmittedTotal() {
        return submittedTotal;
    }

    public Integer getExpectedTotal() {
        return expectedTotal;
    }

    public String getMessage() {
        return message;
    }

    public String getRegNo() {
        return regNo;
    }

    public Integer getTotalPollsMade() {
        return totalPollsMade;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    @Override
    public String toString() {
        return "SubmitResult{" +
                "correct=" + correct +
                ", idempotent=" + idempotent +
                ", submittedTotal=" + submittedTotal +
                ", expectedTotal=" + expectedTotal +
                ", message='" + message + '\'' +
                ", regNo='" + regNo + '\'' +
                ", totalPollsMade=" + totalPollsMade +
                ", attemptCount=" + attemptCount +
                '}';
    }
}
