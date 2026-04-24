param(
    [Parameter(Mandatory = $true)]
    [string]$RegNo,
    [string]$BaseUrl = "https://devapigw.vidalhealthtpa.com/srm-quiz-task"
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

if (Test-Path "out") {
    Remove-Item -Recurse -Force "out"
}
New-Item -ItemType Directory -Path "out" | Out-Null

$sources = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
javac -d "out" $sources

java -cp "out" com.bajajfinserv.quiz.QuizLeaderboardApp $RegNo $BaseUrl
