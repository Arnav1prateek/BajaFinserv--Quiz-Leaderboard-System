# Quiz Leaderboard System

A Java-based backend assignment solution that polls quiz events from a validator API, removes duplicate events, computes participant totals, generates a leaderboard, and submits the result.

## Assignment Objective

This project implements the required flow from the SRM Java/Salesforce internship problem statement:

1. Poll the validator API exactly 10 times (`poll=0` to `poll=9`)
2. Maintain a 5-second delay between each poll
3. Collect all events
4. Deduplicate data using `roundId + participant`
5. Aggregate scores per participant
6. Sort leaderboard by `totalScore` (descending)
7. Compute final total score across all users
8. Submit leaderboard once to validator

## API Endpoints Used

Base URL:

`https://devapigw.vidalhealthtpa.com/srm-quiz-task`

Endpoints:

- `GET /quiz/messages?regNo=<REG_NO>&poll=<0-9>`
- `POST /quiz/submit`

## Tech Stack

- Java 17
- Java `HttpClient` (`java.net.http`)
- PowerShell helper script for compile + run

No external Java dependencies are required.

## Project Structure

```text
Quiz Leaderboard System/
├─ src/com/bajajfinserv/quiz/
│  ├─ QuizLeaderboardApp.java
│  ├─ client/
│  │  └─ QuizApiClient.java
│  ├─ parser/
│  │  └─ QuizJsonParser.java
│  ├─ service/
│  │  └─ LeaderboardService.java
│  └─ model/
│     ├─ PollMessage.java
│     ├─ QuizEvent.java
│     ├─ LeaderboardEntry.java
│     └─ SubmitResult.java
├─ scripts/
│  └─ run.ps1
└─ README.md
```

## How It Works

### 1) Polling

`QuizLeaderboardApp` calls the validator API 10 times in order (`0..9`).

### 2) Delay Compliance

The app enforces a `5000 ms` delay between polls to satisfy mandatory timing.

### 3) Deduplication

`LeaderboardService` keeps a set of unique keys:

`roundId + "::" + participant`

If the same key appears again in later polls, it is ignored.

### 4) Aggregation + Sorting

Scores are aggregated per participant and converted into leaderboard entries sorted by:

1. `totalScore` descending
2. participant name ascending (tie-breaker)

### 5) Submission

The final leaderboard is submitted once using `POST /quiz/submit`.

## Prerequisites

Before running:

- Java 17 installed and available on PATH
- PowerShell terminal (Windows)
- Internet access to validator API host

Check Java:

```powershell
java -version
```

## Run Instructions (Windows / PowerShell)

From repository root:

```powershell
cd "D:\BajajFinserv Proj\Quiz Leaderboard System"
.\scripts\run.ps1 -RegNo "YOUR_REG_NO"
```

Example:

```powershell
.\scripts\run.ps1 -RegNo "2024CS101"
```

Optional custom base URL:

```powershell
.\scripts\run.ps1 -RegNo "2024CS101" -BaseUrl "https://devapigw.vidalhealthtpa.com/srm-quiz-task"
```

## Expected Console Output

You should see:

- Polling progress for indices `0` to `9`
- Events received per poll
- Final leaderboard ranking
- Total score across all users
- Submission response fields returned by live API

Note: The validator response format can vary over time. This project handles both the sample-style fields (`isCorrect`, `expectedTotal`) and currently observed live fields (`regNo`, `totalPollsMade`, `attemptCount`, `submittedTotal`).

## Quick Validation Checklist

Use this checklist before sharing or submitting:

- 10 polls executed
- 5-second delay respected
- duplicate events ignored using `roundId + participant`
- leaderboard sorted correctly by total score
- total score printed
- submit call executed once at the end

## Troubleshooting

### Script execution blocked

If PowerShell blocks script execution:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

Then rerun the script.

### Network/API connectivity issues

If the API is unreachable:

- Check internet connection
- Check firewall/proxy restrictions
- Retry after a short delay

### Invalid or unregistered `regNo`

If validator behavior looks unexpected, confirm that your registration number is valid for the assignment/test.

## Notes

- This implementation is intentionally dependency-light and easy to run on a fresh machine.
- Polling and submission behavior are aligned with the assignment rules from the provided PDF.

