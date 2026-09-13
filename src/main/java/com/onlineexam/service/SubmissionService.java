package com.onlineexam.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.onlineexam.dao.AnswerDAO;
import com.onlineexam.dao.AttemptDAO;
import com.onlineexam.dao.DataAccessException;
import com.onlineexam.dao.ExamQuestionDAO;
import com.onlineexam.dao.ResultDAO;
import com.onlineexam.model.Exam;
import com.onlineexam.model.ExamAttempt;
import com.onlineexam.model.Question;
import com.onlineexam.model.Result;
import com.onlineexam.model.StudentAnswer;
import com.onlineexam.util.DBConnection;

/**
 * Encapsulates the one operation that must be atomic: grading a submitted attempt
 * and writing its answers, result, and SUBMITTED status together. Kept out of the
 * servlets so both the normal submit and the server-side timeout path can share it
 * (and so the transaction lives in exactly one place).
 *
 * <p>Grading is entirely server-side: the correct answers are read from the DB here
 * and never trusted from the browser. The result row is a snapshot (marks + max at
 * submit time) so later edits to questions/exams can't change a recorded score.</p>
 */
public class SubmissionService {

    private final ExamQuestionDAO examQuestionDAO = new ExamQuestionDAO();
    private final AnswerDAO answerDAO = new AnswerDAO();
    private final ResultDAO resultDAO = new ResultDAO();
    private final AttemptDAO attemptDAO = new AttemptDAO();

    /**
     * Grade {@code attempt} against {@code selectedByQuestionId} and persist everything
     * in a single transaction.
     *
     * @param attempt              the IN_PROGRESS attempt being finished
     * @param exam                 the exam (for passing marks)
     * @param selectedByQuestionId questionId → chosen option ("A".."D"); a missing
     *                             entry (or an empty map) means unanswered → 0 marks
     * @return the persisted {@link Result}
     */
    public Result gradeAndPersist(ExamAttempt attempt, Exam exam,
                                  Map<Integer, String> selectedByQuestionId) {

        List<Question> questions = examQuestionDAO.findQuestionsForExam(exam.getExamId());

        List<StudentAnswer> answers = new ArrayList<>();
        int obtained = 0;
        int max = 0;
        for (Question q : questions) {
            max += q.getMarks();
            String selected = selectedByQuestionId.get(q.getQuestionId());
            boolean correct = selected != null && selected.equals(q.getCorrectOption());
            int marks = correct ? q.getMarks() : 0;
            obtained += marks;
            answers.add(new StudentAnswer(
                    attempt.getAttemptId(), q.getQuestionId(), selected, correct, marks));
        }

        double percentage = (max > 0) ? Math.round(obtained * 10000.0 / max) / 100.0 : 0.0;
        String status = (obtained >= exam.getPassingMarks()) ? "PASS" : "FAIL";
        LocalDateTime now = LocalDateTime.now();

        Result result = new Result();
        result.setAttemptId(attempt.getAttemptId());
        result.setUserId(attempt.getUserId());
        result.setExamId(exam.getExamId());
        result.setMarksObtained(obtained);
        result.setMaxMarks(max);
        result.setPercentage(percentage);
        result.setStatus(status);
        result.setResultDate(now);

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);                       // begin transaction

            answerDAO.saveAll(answers, con);                // 1) all answers
            resultDAO.insert(result, con);                  // 2) the result row
            attemptDAO.markSubmitted(attempt.getAttemptId(), now, con); // 3) status

            con.commit();                                   // all-or-nothing
        } catch (SQLException | RuntimeException e) {
            rollbackQuietly(con);
            throw new DataAccessException("Failed to persist exam submission", e);
        } finally {
            closeQuietly(con);
        }
        return result;
    }

    private void rollbackQuietly(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ignore) {
                // nothing useful to do here
            }
        }
    }

    private void closeQuietly(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ignore) {
                // ignore
            }
            try {
                con.close();
            } catch (SQLException ignore) {
                // ignore
            }
        }
    }
}
