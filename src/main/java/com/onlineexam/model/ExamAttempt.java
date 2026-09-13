package com.onlineexam.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * One attempt of an exam by a student. {@link #startTime} is server-recorded and
 * is the authoritative basis for timer enforcement. {@link #examName} is a
 * convenience field (not a column) populated when listing a student's history.
 */
public class ExamAttempt {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private int attemptId;
    private int userId;
    private int examId;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private String status; // "IN_PROGRESS" | "SUBMITTED"

    // convenience (from join)
    private String examName;

    public ExamAttempt() {
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public boolean isSubmitted() {
        return "SUBMITTED".equals(status);
    }

    public String getStartTimeFormatted() {
        return startTime == null ? "" : startTime.format(FMT);
    }

    public String getSubmitTimeFormatted() {
        return submitTime == null ? "—" : submitTime.format(FMT);
    }
}
