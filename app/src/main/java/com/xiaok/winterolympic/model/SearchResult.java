package com.xiaok.winterolympic.model;

public class SearchResult {
    private String matchName;
    private String matchDetails;
    private String mathchDate;
    private String matchTime;
    private String matchPosition;

    public SearchResult(String matchName, String matchDetails, String mathchDate, String matchTime, String matchPosition) {
        this.matchName = matchName;
        this.matchDetails = matchDetails;
        this.mathchDate = mathchDate;
        this.matchTime = matchTime;
        this.matchPosition = matchPosition;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public void setMatchDetails(String matchDetails) {
        this.matchDetails = matchDetails;
    }

    public void setMathchDate(String mathchDate) {
        this.mathchDate = mathchDate;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getMatchName() {
        return matchName;
    }

    public String getMatchDetails() {
        return matchDetails;
    }

    public String getMathchDate() {
        return mathchDate;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public String getMatchPosition() {
        return matchPosition;
    }

    public void setMatchPosition(String matchPosition) {

        this.matchPosition = matchPosition;
    }
}
