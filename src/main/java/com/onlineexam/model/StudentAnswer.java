package com.onlineexam.model;

/**
 * A student's answer to one question within one attempt, together with the
 * evaluated outcome ({@link #isCorrect}, {@link #marksObtained}) which is
 * snapshotted at submission time so later question edits cannot change it.
 */
public class StudentAnswer {

    private int answerId;
    private int attemptId;
    private int questionId;
    private String selectedOption; // "A".."D" or null when unanswered
    private boolean isCorrect;
    private int marksObtained;

    public StudentAnswer() {
    }

    public StudentAnswer(int attemptId, int questionId, String selectedOption,
                         boolean isCorrect, int marksObtained) {
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.isCorrect = isCorrect;
        this.marksObtained = marksObtained;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public int getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(int marksObtained) {
        this.marksObtained = marksObtained;
    }
}
