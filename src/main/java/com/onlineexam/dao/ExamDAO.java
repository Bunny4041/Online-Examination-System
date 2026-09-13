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

import com.onlineexam.model.Exam;
import com.onlineexam.util.DBConnection;

/**
 * Data access for the {@code exams} table.
 */
public class ExamDAO {

    public int insert(Exam e) {
        String sql = "INSERT INTO exams "
                   + "(exam_name, description, duration_min, start_date, end_date, "
                   + " max_marks, passing_marks, status, created_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getExamName());
            ps.setString(2, e.getDescription());
            ps.setInt(3, e.getDurationMin());
            ps.setTimestamp(4, Timestamp.valueOf(e.getStartDate()));
            ps.setTimestamp(5, Timestamp.valueOf(e.getEndDate()));
            ps.setInt(6, e.getMaxMarks());
            ps.setInt(7, e.getPassingMarks());
            ps.setString(8, e.getStatus() == null ? "DRAFT" : e.getStatus());
            if (e.getCreatedBy() > 0) {
                ps.setInt(9, e.getCreatedBy());
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to insert exam", ex);
        }
    }

    public boolean update(Exam e) {
        String sql = "UPDATE exams SET exam_name = ?, description = ?, duration_min = ?, "
                   + "start_date = ?, end_date = ?, max_marks = ?, passing_marks = ? "
                   + "WHERE exam_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getExamName());
            ps.setString(2, e.getDescription());
            ps.setInt(3, e.getDurationMin());
            ps.setTimestamp(4, Timestamp.valueOf(e.getStartDate()));
            ps.setTimestamp(5, Timestamp.valueOf(e.getEndDate()));
            ps.setInt(6, e.getMaxMarks());
            ps.setInt(7, e.getPassingMarks());
            ps.setInt(8, e.getExamId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to update exam", ex);
        }
    }

    public Exam findById(int examId) {
        String sql = "SELECT * FROM exams WHERE exam_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to find exam by id", ex);
        }
    }

    public List<Exam> findAll() {
        String sql = "SELECT * FROM exams ORDER BY exam_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapList(rs);
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to list exams", ex);
        }
    }

    public List<Exam> findByStatus(String status) {
        String sql = "SELECT * FROM exams WHERE status = ? ORDER BY exam_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to list exams by status", ex);
        }
    }

    /** PUBLISHED exams whose availability window contains {@code now}. */
    public List<Exam> findAvailableForStudents(LocalDateTime now) {
        String sql = "SELECT * FROM exams WHERE status = 'PUBLISHED' "
                   + "AND start_date <= ? AND end_date >= ? ORDER BY end_date ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            Timestamp ts = Timestamp.valueOf(now);
            ps.setTimestamp(1, ts);
            ps.setTimestamp(2, ts);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to list available exams", ex);
        }
    }

    public List<Exam> search(String keyword) {
        String sql = "SELECT * FROM exams WHERE exam_name LIKE ? OR CAST(exam_id AS CHAR) = ? "
                   + "ORDER BY exam_id DESC";
        String k = keyword == null ? "" : keyword.trim();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + k + "%");
            ps.setString(2, k);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to search exams", ex);
        }
    }

    public boolean updateStatus(int examId, String status) {
        String sql = "UPDATE exams SET status = ? WHERE exam_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, examId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to update exam status", ex);
        }
    }

    private List<Exam> mapList(ResultSet rs) throws SQLException {
        List<Exam> list = new ArrayList<>();
        while (rs.next()) {
            list.add(map(rs));
        }
        return list;
    }

    private Exam map(ResultSet rs) throws SQLException {
        Exam e = new Exam();
        e.setExamId(rs.getInt("exam_id"));
        e.setExamName(rs.getString("exam_name"));
        e.setDescription(rs.getString("description"));
        e.setDurationMin(rs.getInt("duration_min"));
        Timestamp sd = rs.getTimestamp("start_date");
        Timestamp ed = rs.getTimestamp("end_date");
        e.setStartDate(sd == null ? null : sd.toLocalDateTime());
        e.setEndDate(ed == null ? null : ed.toLocalDateTime());
        e.setMaxMarks(rs.getInt("max_marks"));
        e.setPassingMarks(rs.getInt("passing_marks"));
        e.setStatus(rs.getString("status"));
        e.setCreatedBy(rs.getInt("created_by"));
        Timestamp ca = rs.getTimestamp("created_at");
        e.setCreatedAt(ca == null ? null : ca.toLocalDateTime());
        return e;
    }
}
