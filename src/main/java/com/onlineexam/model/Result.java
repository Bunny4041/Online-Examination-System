package com.onlineexam.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The final scorecard for an attempt (1:1 with an attempt). {@link #examName} and
 * {@link #studentName} are convenience fields (not columns) populated by joins
 * for list screens (My Results / admin results).
 */
public class Result {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private int resultId;
    private int attemptId;
    private int userId;
    private int examId;
    private int marksObtained;
    private int maxMarks;
    private double percentage;
    private String status; // "PASS" | "FAIL"
    private LocalDateTime resultDate;

    // convenience (from joins)
    private String examName;
    private String studentName;
    private String studentUsername;

    public Result() {
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(int marksObtained) {
        this.marksObtained = marksObtained;
    }

    public int getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(int maxMarks) {
        this.maxMarks = maxMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getResultDate() {
        return resultDate;
    }

    public void setResultDate(LocalDateTime resultDate) {
        this.resultDate = resultDate;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public boolean isPass() {
        return "PASS".equals(status);
    }

    public String getResultDateFormatted() {
        return resultDate == null ? "" : resultDate.format(FMT);
    }
}
