package com.bajajfinserv.quiz.model;

public class LeaderboardEntry {
    private final String participant;
    private final int totalScore;

    public LeaderboardEntry(String participant, int totalScore) {
        this.participant = participant;
        this.totalScore = totalScore;
    }

    public String getParticipant() {
        return participant;
    }

    public int getTotalScore() {
        return totalScore;
    }

    @Override
    public String toString() {
        return "LeaderboardEntry{" +
                "participant='" + participant + '\'' +
                ", totalScore=" + totalScore +
                '}';
    }
}
