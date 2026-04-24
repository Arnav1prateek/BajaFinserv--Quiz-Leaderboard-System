package com.bajajfinserv.quiz.service;

import com.bajajfinserv.quiz.model.LeaderboardEntry;
import com.bajajfinserv.quiz.model.PollMessage;
import com.bajajfinserv.quiz.model.QuizEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LeaderboardService {
    public List<LeaderboardEntry> buildLeaderboard(List<PollMessage> pollMessages) {
        Map<String, Integer> totalsByParticipant = new HashMap<>();
        Set<String> seenRoundParticipant = new HashSet<>();

        for (PollMessage pollMessage : pollMessages) {
            for (QuizEvent event : pollMessage.getEvents()) {
                String uniqueKey = event.getRoundId() + "::" + event.getParticipant();
                if (seenRoundParticipant.contains(uniqueKey)) {
                    continue;
                }

                seenRoundParticipant.add(uniqueKey);
                totalsByParticipant.merge(event.getParticipant(), event.getScore(), Integer::sum);
            }
        }

        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : totalsByParticipant.entrySet()) {
            leaderboard.add(new LeaderboardEntry(entry.getKey(), entry.getValue()));
        }

        leaderboard.sort(
                Comparator.comparingInt(LeaderboardEntry::getTotalScore).reversed()
                        .thenComparing(LeaderboardEntry::getParticipant)
        );
        return leaderboard;
    }

    public int totalAcrossAllUsers(List<LeaderboardEntry> leaderboard) {
        int total = 0;
        for (LeaderboardEntry entry : leaderboard) {
            total += entry.getTotalScore();
        }
        return total;
    }
}
