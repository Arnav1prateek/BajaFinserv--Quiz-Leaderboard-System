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
    private static final Pattern EVENT_PATTERN = Pattern.compile(
            "\\{\\s*\"roundId\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"participant\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"score\"\\s*:\\s*(-?\\d+)\\s*\\}"
    );
    private static final Pattern IS_CORRECT_PATTERN = Pattern.compile("\"isCorrect\"\\s*:\\s*(true|false)");
    private static final Pattern IS_IDEMPOTENT_PATTERN = Pattern.compile("\"isIdempotent\"\\s*:\\s*(true|false)");
    private static final Pattern SUBMITTED_TOTAL_PATTERN = Pattern.compile("\"submittedTotal\"\\s*:\\s*(-?\\d+)");
    private static final Pattern EXPECTED_TOTAL_PATTERN = Pattern.compile("\"expectedTotal\"\\s*:\\s*(-?\\d+)");
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]*)\"");

    public PollMessage parsePollMessage(String json) {
        String regNo = findString(REG_NO_PATTERN, json, "regNo");
        String setId = findString(SET_ID_PATTERN, json, "setId");
        int pollIndex = findInt(POLL_INDEX_PATTERN, json, "pollIndex");

        List<QuizEvent> events = new ArrayList<>();
        Matcher eventMatcher = EVENT_PATTERN.matcher(json);
        while (eventMatcher.find()) {
            String roundId = eventMatcher.group(1);
            String participant = eventMatcher.group(2);
            int score = Integer.parseInt(eventMatcher.group(3));
            events.add(new QuizEvent(roundId, participant, score));
        }

        return new PollMessage(regNo, setId, pollIndex, events);
    }

    public SubmitResult parseSubmitResult(String json) {
        boolean isCorrect = findBoolean(IS_CORRECT_PATTERN, json, "isCorrect");
        boolean isIdempotent = findBoolean(IS_IDEMPOTENT_PATTERN, json, "isIdempotent");
        int submittedTotal = findInt(SUBMITTED_TOTAL_PATTERN, json, "submittedTotal");
        int expectedTotal = findInt(EXPECTED_TOTAL_PATTERN, json, "expectedTotal");
        String message = findString(MESSAGE_PATTERN, json, "message");
        return new SubmitResult(isCorrect, isIdempotent, submittedTotal, expectedTotal, message);
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

    private String escapeJson(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
