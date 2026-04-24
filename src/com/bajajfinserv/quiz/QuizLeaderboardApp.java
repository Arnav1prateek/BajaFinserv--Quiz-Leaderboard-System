package com.bajajfinserv.quiz;

import com.bajajfinserv.quiz.client.QuizApiClient;
import com.bajajfinserv.quiz.model.LeaderboardEntry;
import com.bajajfinserv.quiz.model.PollMessage;
import com.bajajfinserv.quiz.model.SubmitResult;
import com.bajajfinserv.quiz.service.LeaderboardService;

import java.util.ArrayList;
import java.util.List;

public class QuizLeaderboardApp {
    private static final String DEFAULT_BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    private static final int POLL_COUNT = 10;
    private static final long DELAY_BETWEEN_POLLS_MS = 5000L;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java com.bajajfinserv.quiz.QuizLeaderboardApp <regNo> [baseUrl]");
            System.exit(1);
        }

        String regNo = args[0].trim();
        String baseUrl = args.length > 1 ? args[1].trim() : DEFAULT_BASE_URL;

        QuizApiClient apiClient = new QuizApiClient(baseUrl);
        LeaderboardService leaderboardService = new LeaderboardService();
        List<PollMessage> pollMessages = new ArrayList<>();

        try {
            for (int pollIndex = 0; pollIndex < POLL_COUNT; pollIndex++) {
                System.out.println("Polling index " + pollIndex + "...");
                PollMessage message = apiClient.fetchPoll(regNo, pollIndex);
                pollMessages.add(message);
                System.out.println("Received " + message.getEvents().size() + " events for poll " + pollIndex);

                if (pollIndex < POLL_COUNT - 1) {
                    Thread.sleep(DELAY_BETWEEN_POLLS_MS);
                }
            }

            List<LeaderboardEntry> leaderboard = leaderboardService.buildLeaderboard(pollMessages);
            int totalScore = leaderboardService.totalAcrossAllUsers(leaderboard);

            System.out.println("\nFinal Leaderboard:");
            for (int i = 0; i < leaderboard.size(); i++) {
                LeaderboardEntry entry = leaderboard.get(i);
                System.out.println((i + 1) + ". " + entry.getParticipant() + " -> " + entry.getTotalScore());
            }
            System.out.println("Total score across all users: " + totalScore);

            SubmitResult submitResult = apiClient.submitLeaderboard(regNo, leaderboard);
            System.out.println("\nSubmission response:");
            if (submitResult.isCorrect() != null) {
                System.out.println("isCorrect: " + submitResult.isCorrect());
            }
            if (submitResult.isIdempotent() != null) {
                System.out.println("isIdempotent: " + submitResult.isIdempotent());
            }
            if (submitResult.getSubmittedTotal() != null) {
                System.out.println("submittedTotal: " + submitResult.getSubmittedTotal());
            }
            if (submitResult.getExpectedTotal() != null) {
                System.out.println("expectedTotal: " + submitResult.getExpectedTotal());
            }
            if (submitResult.getMessage() != null) {
                System.out.println("message: " + submitResult.getMessage());
            }
            if (submitResult.getRegNo() != null) {
                System.out.println("regNo: " + submitResult.getRegNo());
            }
            if (submitResult.getTotalPollsMade() != null) {
                System.out.println("totalPollsMade: " + submitResult.getTotalPollsMade());
            }
            if (submitResult.getAttemptCount() != null) {
                System.out.println("attemptCount: " + submitResult.getAttemptCount());
            }
        } catch (Exception e) {
            System.err.println("Execution failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
