package com.bajajfinserv.quiz.model;

import java.util.Collections;
import java.util.List;

public class PollMessage {
    private final String regNo;
    private final String setId;
    private final int pollIndex;
    private final List<QuizEvent> events;

    public PollMessage(String regNo, String setId, int pollIndex, List<QuizEvent> events) {
        this.regNo = regNo;
        this.setId = setId;
        this.pollIndex = pollIndex;
        this.events = events == null ? List.of() : List.copyOf(events);
    }

    public String getRegNo() {
        return regNo;
    }

    public String getSetId() {
        return setId;
    }

    public int getPollIndex() {
        return pollIndex;
    }

    public List<QuizEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    @Override
    public String toString() {
        return "PollMessage{" +
                "regNo='" + regNo + '\'' +
                ", setId='" + setId + '\'' +
                ", pollIndex=" + pollIndex +
                ", eventsCount=" + events.size() +
                '}';
    }
}
