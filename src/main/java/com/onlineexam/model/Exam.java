package com.onlineexam.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * One examination. Lifecycle: DRAFT -&gt; PUBLISHED -&gt; DEACTIVATED.
 *
 * <p>{@link #questionCount} and {@link #allocatedMarks} are NOT database columns;
 * they are convenience fields a servlet may populate (from ExamQuestionDAO) so a
 * list/details JSP can show "how many questions / do the marks add up" without
 * extra lookups in the page.</p>
 */
public class Exam {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    // HTML <input type="datetime-local"> expects exactly this pattern.
    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private int examId;
    private String examName;
    private String description;
    private int durationMin;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int maxMarks;
    private int passingMarks;
    private String status; // "DRAFT" | "PUBLISHED" | "DEACTIVATED"
    private int createdBy;
    private LocalDateTime createdAt;

    // convenience (not persisted on this row)
    private int questionCount;
    private int allocatedMarks;

    public Exam() {
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(int durationMin) {
        this.durationMin = durationMin;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(int maxMarks) {
        this.maxMarks = maxMarks;
    }

    public int getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(int passingMarks) {
        this.passingMarks = passingMarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getAllocatedMarks() {
        return allocatedMarks;
    }

    public void setAllocatedMarks(int allocatedMarks) {
        this.allocatedMarks = allocatedMarks;
    }

    // ---- display helpers (used by JSPs) ----

    public String getStartDateFormatted() {
        return startDate == null ? "" : startDate.format(FMT);
    }

    public String getEndDateFormatted() {
        return endDate == null ? "" : endDate.format(FMT);
    }

    /** Value for an HTML datetime-local input (empty when unset). */
    public String getStartDateForInput() {
        return startDate == null ? "" : startDate.format(INPUT_FMT);
    }

    /** Value for an HTML datetime-local input (empty when unset). */
    public String getEndDateForInput() {
        return endDate == null ? "" : endDate.format(INPUT_FMT);
    }

    public boolean isPublished() {
        return "PUBLISHED".equals(status);
    }

    public boolean isDraft() {
        return "DRAFT".equals(status);
    }

    public boolean isDeactivated() {
        return "DEACTIVATED".equals(status);
    }
}
