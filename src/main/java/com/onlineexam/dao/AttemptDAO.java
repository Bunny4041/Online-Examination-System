package com.onlineexam.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.onlineexam.model.ExamAttempt;
import com.onlineexam.util.DBConnection;

/**
 * Data access for {@code exam_attempts}. One row is created when a student starts
 * an exam and is later marked SUBMITTED. The single-attempt rule is enforced both
 * by {@link #hasAttempted(int, int)} (checked before starting) and by a UNIQUE
 * (user_id, exam_id) constraint in the schema (last line of defence).
 */
public class AttemptDAO {

    public int insert(ExamAttempt a) {
        String sql = "INSERT INTO exam_attempts (user_id, exam_id, start_time, status) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getUserId());
            ps.setInt(2, a.getExamId());
            ps.setTimestamp(3, Timestamp.valueOf(
                    a.getStartTime() == null ? LocalDateTime.now() : a.getStartTime()));
            ps.setString(4, a.getStatus() == null ? "IN_PROGRESS" : a.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert attempt", e);
        }
    }

    public ExamAttempt findById(int attemptId) {
        String sql = "SELECT * FROM exam_attempts WHERE attempt_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find attempt by id", e);
        }
    }

    public ExamAttempt findByUserAndExam(int userId, int examId) {
        String sql = "SELECT * FROM exam_attempts WHERE user_id = ? AND exam_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, examId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find attempt by user and exam", e);
        }
    }

    public boolean hasAttempted(int userId, int examId) {
        String sql = "SELECT 1 FROM exam_attempts WHERE user_id = ? AND exam_id = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, examId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check prior attempt", e);
        }
    }

    /** Standalone version (opens and closes its own connection). */
    public boolean markSubmitted(int attemptId, LocalDateTime submitTime) {
        try (Connection con = DBConnection.getConnection()) {
            return markSubmitted(attemptId, submitTime, con);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to mark attempt submitted", e);
        }
    }

    /**
     * Transaction-aware version: uses the caller's connection and does NOT close it,
     * so it can share the submit transaction with the answer/result inserts.
     */
    public boolean markSubmitted(int attemptId, LocalDateTime submitTime, Connection con) {
        String sql = "UPDATE exam_attempts SET status = 'SUBMITTED', submit_time = ? "
                   + "WHERE attempt_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(
                    submitTime == null ? LocalDateTime.now() : submitTime));
            ps.setInt(2, attemptId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to mark attempt submitted", e);
        }
    }

    /** A student's attempts, newest first, with exam name joined for display. */
    public List<ExamAttempt> findByUser(int userId) {
        String sql = "SELECT a.*, e.exam_name FROM exam_attempts a "
                   + "JOIN exams e ON e.exam_id = a.exam_id "
                   + "WHERE a.user_id = ? ORDER BY a.start_time DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<ExamAttempt> list = new ArrayList<>();
                while (rs.next()) {
                    ExamAttempt a = map(rs);
                    a.setExamName(rs.getString("exam_name"));
                    list.add(a);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list attempts by user", e);
        }
    }

    private ExamAttempt map(ResultSet rs) throws SQLException {
        ExamAttempt a = new ExamAttempt();
        a.setAttemptId(rs.getInt("attempt_id"));
        a.setUserId(rs.getInt("user_id"));
        a.setExamId(rs.getInt("exam_id"));
        Timestamp st = rs.getTimestamp("start_time");
        Timestamp sub = rs.getTimestamp("submit_time");
        a.setStartTime(st == null ? null : st.toLocalDateTime());
        a.setSubmitTime(sub == null ? null : sub.toLocalDateTime());
        a.setStatus(rs.getString("status"));
        return a;
    }
}
