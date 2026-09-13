package com.onlineexam.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.onlineexam.model.StudentAnswer;
import com.onlineexam.util.DBConnection;

/**
 * Data access for {@code student_answers}. Uses INSERT ... ON DUPLICATE KEY UPDATE
 * against the UNIQUE (attempt_id, question_id) constraint so re-saving an answer
 * updates the existing row instead of erroring.
 */
public class AnswerDAO {

    private static final String UPSERT =
            "INSERT INTO student_answers "
          + "(attempt_id, question_id, selected_option, is_correct, marks_obtained) "
          + "VALUES (?, ?, ?, ?, ?) "
          + "ON DUPLICATE KEY UPDATE "
          + "selected_option = VALUES(selected_option), "
          + "is_correct = VALUES(is_correct), "
          + "marks_obtained = VALUES(marks_obtained)";

    /** Save (or update) one answer using its own connection. */
    public boolean saveOrUpdate(StudentAnswer a) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPSERT)) {
            bind(ps, a);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save answer", e);
        }
    }

    /**
     * Save all answers on the caller's connection as part of the submit transaction.
     * Does NOT close the connection or manage commit/rollback — the caller owns that.
     */
    public void saveAll(List<StudentAnswer> answers, Connection con) {
        if (answers == null || answers.isEmpty()) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement(UPSERT)) {
            for (StudentAnswer a : answers) {
                bind(ps, a);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save answers batch", e);
        }
    }

    public List<StudentAnswer> findByAttempt(int attemptId) {
        String sql = "SELECT * FROM student_answers WHERE attempt_id = ? ORDER BY answer_id ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                List<StudentAnswer> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load answers for attempt", e);
        }
    }

    private void bind(PreparedStatement ps, StudentAnswer a) throws SQLException {
        ps.setInt(1, a.getAttemptId());
        ps.setInt(2, a.getQuestionId());
        if (a.getSelectedOption() == null) {
            ps.setNull(3, Types.CHAR);
        } else {
            ps.setString(3, a.getSelectedOption());
        }
        ps.setBoolean(4, a.isCorrect());
        ps.setInt(5, a.getMarksObtained());
    }

    private StudentAnswer map(ResultSet rs) throws SQLException {
        StudentAnswer a = new StudentAnswer();
        a.setAnswerId(rs.getInt("answer_id"));
        a.setAttemptId(rs.getInt("attempt_id"));
        a.setQuestionId(rs.getInt("question_id"));
        a.setSelectedOption(rs.getString("selected_option"));
        a.setCorrect(rs.getBoolean("is_correct"));
        a.setMarksObtained(rs.getInt("marks_obtained"));
        return a;
    }
}
