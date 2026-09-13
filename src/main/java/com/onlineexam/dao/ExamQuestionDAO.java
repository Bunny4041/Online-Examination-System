package com.onlineexam.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.onlineexam.model.Question;
import com.onlineexam.util.DBConnection;

/**
 * Data access for the {@code exam_questions} junction (which questions belong to
 * which exam, and in what order).
 */
public class ExamQuestionDAO {

    public int allocate(int examId, int questionId, int order) {
        String sql = "INSERT INTO exam_questions (exam_id, question_id, question_order) "
                   + "VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, examId);
            ps.setInt(2, questionId);
            ps.setInt(3, order);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to allocate question to exam", e);
        }
    }

    public boolean deallocate(int examId, int questionId) {
        String sql = "DELETE FROM exam_questions WHERE exam_id = ? AND question_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setInt(2, questionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to deallocate question", e);
        }
    }

    public boolean isAllocated(int examId, int questionId) {
        String sql = "SELECT 1 FROM exam_questions WHERE exam_id = ? AND question_id = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setInt(2, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed allocation check", e);
        }
    }

    /**
     * Full {@link Question} rows (including correct answers) allocated to the exam,
     * in display order. Callers that send these to the browser must strip the
     * correct answer first (see Question.toSafeView()).
     */
    public List<Question> findQuestionsForExam(int examId) {
        String sql = "SELECT q.* FROM exam_questions eq "
                   + "JOIN questions q ON q.question_id = eq.question_id "
                   + "WHERE eq.exam_id = ? "
                   + "ORDER BY eq.question_order ASC, eq.exam_question_id ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Question> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load exam questions", e);
        }
    }

    public int countByExam(int examId) {
        String sql = "SELECT COUNT(*) FROM exam_questions WHERE exam_id = ?";
        return intQuery(sql, examId);
    }

    public int sumMarksByExam(int examId) {
        String sql = "SELECT COALESCE(SUM(q.marks), 0) FROM exam_questions eq "
                   + "JOIN questions q ON q.question_id = eq.question_id "
                   + "WHERE eq.exam_id = ?";
        return intQuery(sql, examId);
    }

    private int intQuery(String sql, int examId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Aggregate query failed", e);
        }
    }

    private Question map(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setQuestionId(rs.getInt("question_id"));
        q.setQuestionText(rs.getString("question_text"));
        q.setOptionA(rs.getString("option_a"));
        q.setOptionB(rs.getString("option_b"));
        q.setOptionC(rs.getString("option_c"));
        q.setOptionD(rs.getString("option_d"));
        q.setCorrectOption(rs.getString("correct_option"));
        q.setMarks(rs.getInt("marks"));
        q.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        q.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        return q;
    }
}
