package com.bajajfinserv.quiz.parser;

import com.bajajfinserv.quiz.model.LeaderboardEntry;
import com.bajajfinserv.quiz.model.PollMessage;
import com.bajajfinserv.quiz.model.QuizEvent;
import com.bajajfinserv.quiz.model.SubmitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuizJsonParser {
    private static final Pattern REG_NO_PATTERN = Pattern.compile("\"regNo\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern SET_ID_PATTERN = Pattern.compile("\"setId\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern POLL_INDEX_PATTERN = Pattern.compile("\"pollIndex\"\\s*:\\s*(-?\\d+)");
    private static final Pattern EVENTS_ARRAY_PATTERN = Pattern.compile("\"events\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL);
    private static final Pattern EVENT_OBJECT_PATTERN = Pattern.compile("\\{(.*?)}", Pattern.DOTALL);
    private static final Pattern ROUND_ID_PATTERN = Pattern.compile("\"roundId\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern PARTICIPANT_PATTERN = Pattern.compile("\"participant\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern SCORE_PATTERN = Pattern.compile("\"score\"\\s*:\\s*(-?\\d+)");
    private static final Pattern IS_CORRECT_PATTERN = Pattern.compile("\"isCorrect\"\\s*:\\s*(true|false)");
    private static final Pattern IS_IDEMPOTENT_PATTERN = Pattern.compile("\"isIdempotent\"\\s*:\\s*(true|false)");
    private static final Pattern SUBMITTED_TOTAL_PATTERN = Pattern.compile("\"submittedTotal\"\\s*:\\s*(-?\\d+)");
    private static final Pattern EXPECTED_TOTAL_PATTERN = Pattern.compile("\"expectedTotal\"\\s*:\\s*(-?\\d+)");
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern TOTAL_POLLS_MADE_PATTERN = Pattern.compile("\"totalPollsMade\"\\s*:\\s*(-?\\d+)");
    private static final Pattern ATTEMPT_COUNT_PATTERN = Pattern.compile("\"attemptCount\"\\s*:\\s*(-?\\d+)");

    public PollMessage parsePollMessage(String json) {
        String regNo = findString(REG_NO_PATTERN, json, "regNo");
        String setId = findOptionalString(SET_ID_PATTERN, json);
        int pollIndex = findInt(POLL_INDEX_PATTERN, json, "pollIndex");
        List<QuizEvent> events = parseEvents(json);

        return new PollMessage(regNo, setId, pollIndex, events);
    }

    public SubmitResult parseSubmitResult(String json) {
        Boolean isCorrect = findOptionalBoolean(IS_CORRECT_PATTERN, json);
        Boolean isIdempotent = findOptionalBoolean(IS_IDEMPOTENT_PATTERN, json);
        Integer submittedTotal = findOptionalInt(SUBMITTED_TOTAL_PATTERN, json);
        Integer expectedTotal = findOptionalInt(EXPECTED_TOTAL_PATTERN, json);
        String message = findOptionalString(MESSAGE_PATTERN, json);
        String regNo = findOptionalString(REG_NO_PATTERN, json);
        Integer totalPollsMade = findOptionalInt(TOTAL_POLLS_MADE_PATTERN, json);
        Integer attemptCount = findOptionalInt(ATTEMPT_COUNT_PATTERN, json);
        return new SubmitResult(isCorrect, isIdempotent, submittedTotal, expectedTotal, message, regNo, totalPollsMade, attemptCount);
    }

    public String buildSubmitPayload(String regNo, List<LeaderboardEntry> leaderboard) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"regNo\":\"")
                .append(escapeJson(regNo))
                .append("\",\"leaderboard\":[");

        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderboardEntry entry = leaderboard.get(i);
            builder.append("{\"participant\":\"")
                    .append(escapeJson(entry.getParticipant()))
                    .append("\",\"totalScore\":")
                    .append(entry.getTotalScore())
                    .append("}");
            if (i < leaderboard.size() - 1) {
                builder.append(",");
            }
        }

        builder.append("]}");
        return builder.toString();
    }

    private String findString(Pattern pattern, String input, String fieldName) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Missing field in JSON: " + fieldName);
    }

    private String findOptionalString(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private int findInt(Pattern pattern, String input, String fieldName) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("Missing field in JSON: " + fieldName);
    }

    private boolean findBoolean(Pattern pattern, String input, String fieldName) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        throw new IllegalArgumentException("Missing field in JSON: " + fieldName);
    }

    private Boolean findOptionalBoolean(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return null;
    }

    private String escapeJson(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private List<QuizEvent> parseEvents(String json) {
        List<QuizEvent> events = new ArrayList<>();
        Matcher arrayMatcher = EVENTS_ARRAY_PATTERN.matcher(json);
        if (!arrayMatcher.find()) {
            return events;
        }

        String eventsBody = arrayMatcher.group(1);
        Matcher objectMatcher = EVENT_OBJECT_PATTERN.matcher(eventsBody);
        while (objectMatcher.find()) {
            String eventJson = objectMatcher.group(1);
            String roundId = findOptionalString(ROUND_ID_PATTERN, eventJson);
            String participant = findOptionalString(PARTICIPANT_PATTERN, eventJson);
            Integer score = findOptionalInt(SCORE_PATTERN, eventJson);
            if (roundId != null && participant != null && score != null) {
                events.add(new QuizEvent(roundId, participant, score));
            }
        }
        return events;
    }

    private Integer findOptionalInt(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }
}
