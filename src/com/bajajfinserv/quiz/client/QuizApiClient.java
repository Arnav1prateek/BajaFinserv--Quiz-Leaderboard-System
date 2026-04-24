package com.bajajfinserv.quiz.client;

import com.bajajfinserv.quiz.model.LeaderboardEntry;
import com.bajajfinserv.quiz.model.PollMessage;
import com.bajajfinserv.quiz.model.SubmitResult;
import com.bajajfinserv.quiz.parser.QuizJsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class QuizApiClient {
    private final HttpClient httpClient;
    private final String baseUrl;
    private final QuizJsonParser parser;

    public QuizApiClient(String baseUrl) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.baseUrl = stripTrailingSlash(baseUrl);
        this.parser = new QuizJsonParser();
    }

    public PollMessage fetchPoll(String regNo, int pollIndex) throws IOException, InterruptedException {
        String encodedRegNo = URLEncoder.encode(regNo, StandardCharsets.UTF_8);
        URI uri = URI.create(baseUrl + "/quiz/messages?regNo=" + encodedRegNo + "&poll=" + pollIndex);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response, "GET /quiz/messages");
        return parser.parsePollMessage(response.body());
    }

    public SubmitResult submitLeaderboard(String regNo, List<LeaderboardEntry> leaderboard) throws IOException, InterruptedException {
        String payload = parser.buildSubmitPayload(regNo, leaderboard);
        URI uri = URI.create(baseUrl + "/quiz/submit");
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response, "POST /quiz/submit");
        return parser.parseSubmitResult(response.body());
    }

    private void ensureSuccess(HttpResponse<String> response, String operation) {
        int status = response.statusCode();
        if (status < 200 || status > 299) {
            String body = response.body() == null ? "" : response.body();
            throw new IllegalStateException(operation + " failed with status " + status + ". Body: " + body);
        }
    }

    private String stripTrailingSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}
