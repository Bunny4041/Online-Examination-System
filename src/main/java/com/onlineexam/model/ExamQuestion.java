package com.onlineexam.model;

/**
 * Junction row linking a {@link Question} to an {@link Exam} in a given order.
 * When loaded via a JOIN, {@link #question} may carry the full question.
 */
public class ExamQuestion {

    private int examQuestionId;
    private int examId;
    private int questionId;
    private int questionOrder;

    // optional convenience when loaded via join
    private Question question;

    public ExamQuestion() {
    }

    public int getExamQuestionId() {
        return examQuestionId;
    }

    public void setExamQuestionId(int examQuestionId) {
        this.examQuestionId = examQuestionId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(int questionOrder) {
        this.questionOrder = questionOrder;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
