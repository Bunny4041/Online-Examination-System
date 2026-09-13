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

import com.onlineexam.model.Result;
import com.onlineexam.util.DBConnection;

/**
 * Data access for {@code results}. The result row is a snapshot: it stores
 * marks_obtained / max_marks / percentage at submit time so that later edits to
 * exams or questions never change a student's recorded score.
 */
public class ResultDAO {

    /**
     * Insert on the caller's connection as part of the submit transaction.
     * Does NOT close the connection or commit — the caller owns the transaction.
     */
    public int insert(Result r, Connection con) {
        String sql = "INSERT INTO results "
                   + "(attempt_id, user_id, exam_id, marks_obtained, max_marks, "
                   + " percentage, status, result_date) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getAttemptId());
            ps.setInt(2, r.getUserId());
            ps.setInt(3, r.getExamId());
            ps.setInt(4, r.getMarksObtained());
            ps.setInt(5, r.getMaxMarks());
            ps.setDouble(6, r.getPercentage());
            ps.setString(7, r.getStatus());
            ps.setTimestamp(8, Timestamp.valueOf(
                    r.getResultDate() == null ? LocalDateTime.now() : r.getResultDate()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert result", e);
        }
    }

    public Result findByAttempt(int attemptId) {
        String sql = "SELECT r.*, e.exam_name FROM results r "
                   + "JOIN exams e ON e.exam_id = r.exam_id "
                   + "WHERE r.attempt_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Result r = mapBase(rs);
                    r.setExamName(rs.getString("exam_name"));
                    return r;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find result by attempt", e);
        }
    }

    public List<Result> findByUser(int userId) {
        String sql = "SELECT r.*, e.exam_name FROM results r "
                   + "JOIN exams e ON e.exam_id = r.exam_id "
                   + "WHERE r.user_id = ? ORDER BY r.result_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Result> list = new ArrayList<>();
                while (rs.next()) {
                    Result r = mapBase(rs);
                    r.setExamName(rs.getString("exam_name"));
                    list.add(r);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list results by user", e);
        }
    }

    public List<Result> findAll() {
        String sql = "SELECT r.*, e.exam_name, u.full_name AS student_name, "
                   + "u.username AS student_username FROM results r "
                   + "JOIN exams e ON e.exam_id = r.exam_id "
                   + "JOIN users u ON u.user_id = r.user_id "
                   + "ORDER BY r.result_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapListWithNames(rs);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list all results", e);
        }
    }

    public List<Result> search(String keyword) {
        String sql = "SELECT r.*, e.exam_name, u.full_name AS student_name, "
                   + "u.username AS student_username FROM results r "
                   + "JOIN exams e ON e.exam_id = r.exam_id "
                   + "JOIN users u ON u.user_id = r.user_id "
                   + "WHERE u.full_name LIKE ? OR u.username LIKE ? OR e.exam_name LIKE ? "
                   + "ORDER BY r.result_date DESC";
        String like = "%" + (keyword == null ? "" : keyword) + "%";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                return mapListWithNames(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to search results", e);
        }
    }

    private List<Result> mapListWithNames(ResultSet rs) throws SQLException {
        List<Result> list = new ArrayList<>();
        while (rs.next()) {
            Result r = mapBase(rs);
            r.setExamName(rs.getString("exam_name"));
            r.setStudentName(rs.getString("student_name"));
            r.setStudentUsername(rs.getString("student_username"));
            list.add(r);
        }
        return list;
    }

    private Result mapBase(ResultSet rs) throws SQLException {
        Result r = new Result();
        r.setResultId(rs.getInt("result_id"));
        r.setAttemptId(rs.getInt("attempt_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setExamId(rs.getInt("exam_id"));
        r.setMarksObtained(rs.getInt("marks_obtained"));
        r.setMaxMarks(rs.getInt("max_marks"));
        r.setPercentage(rs.getDouble("percentage"));
        r.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("result_date");
        r.setResultDate(ts == null ? null : ts.toLocalDateTime());
        return r;
    }
}
