package com.bajajfinserv.quiz.model;

public class QuizEvent {
    private final String roundId;
    private final String participant;
    private final int score;

    public QuizEvent(String roundId, String participant, int score) {
        this.roundId = roundId;
        this.participant = participant;
        this.score = score;
    }

    public String getRoundId() {
        return roundId;
    }

    public String getParticipant() {
        return participant;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "QuizEvent{" +
                "roundId='" + roundId + '\'' +
                ", participant='" + participant + '\'' +
                ", score=" + score +
                '}';
    }
}
