package com.onlineexam.model;

import java.time.LocalDateTime;

/**
 * A reusable question in the bank. {@link #correctOption} is stored server-side
 * and must NEVER be sent to the browser while an exam is in progress — the
 * take-exam payload uses a "safe" copy with correctOption set to null.
 */
public class Question {

    private int questionId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption; // "A" | "B" | "C" | "D"  (null in safe views)
    private int marks;
    private String status;        // "ACTIVE" | "INACTIVE"
    private LocalDateTime createdAt;

    public Question() {
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return a copy of this question WITHOUT the correct answer, safe to send to
     *         the browser during an attempt.
     */
    public Question toSafeView() {
        Question q = new Question();
        q.questionId = this.questionId;
        q.questionText = this.questionText;
        q.optionA = this.optionA;
        q.optionB = this.optionB;
        q.optionC = this.optionC;
        q.optionD = this.optionD;
        q.correctOption = null; // stripped
        q.marks = this.marks;
        q.status = this.status;
        return q;
    }
}
