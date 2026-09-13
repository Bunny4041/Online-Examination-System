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
 * Data access for the {@code questions} bank. "Delete" is a soft delete
 * (status -&gt; INACTIVE) so historical answers/results stay valid.
 */
public class QuestionDAO {

    public int insert(Question q) {
        String sql = "INSERT INTO questions "
                   + "(question_text, option_a, option_b, option_c, option_d, "
                   + " correct_option, marks, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, q.getQuestionText());
            ps.setString(2, q.getOptionA());
            ps.setString(3, q.getOptionB());
            ps.setString(4, q.getOptionC());
            ps.setString(5, q.getOptionD());
            ps.setString(6, q.getCorrectOption());
            ps.setInt(7, q.getMarks());
            ps.setString(8, q.getStatus() == null ? "ACTIVE" : q.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert question", e);
        }
    }

    public boolean update(Question q) {
        String sql = "UPDATE questions SET question_text = ?, option_a = ?, option_b = ?, "
                   + "option_c = ?, option_d = ?, correct_option = ?, marks = ? "
                   + "WHERE question_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getQuestionText());
            ps.setString(2, q.getOptionA());
            ps.setString(3, q.getOptionB());
            ps.setString(4, q.getOptionC());
            ps.setString(5, q.getOptionD());
            ps.setString(6, q.getCorrectOption());
            ps.setInt(7, q.getMarks());
            ps.setInt(8, q.getQuestionId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update question", e);
        }
    }

    public Question findById(int questionId) {
        String sql = "SELECT * FROM questions WHERE question_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find question by id", e);
        }
    }

    public List<Question> findAllActive() {
        String sql = "SELECT * FROM questions WHERE status = 'ACTIVE' ORDER BY question_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapList(rs);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list active questions", e);
        }
    }

    public List<Question> search(String keyword) {
        String sql = "SELECT * FROM questions WHERE status = 'ACTIVE' AND question_text LIKE ? "
                   + "ORDER BY question_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + (keyword == null ? "" : keyword) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to search questions", e);
        }
    }

    public boolean softDelete(int questionId) {
        String sql = "UPDATE questions SET status = 'INACTIVE' WHERE question_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to soft-delete question", e);
        }
    }

    private List<Question> mapList(ResultSet rs) throws SQLException {
        List<Question> list = new ArrayList<>();
        while (rs.next()) {
            list.add(map(rs));
        }
        return list;
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
