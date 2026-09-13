package com.onlineexam.servlet.student;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.onlineexam.dao.AttemptDAO;
import com.onlineexam.dao.ExamDAO;
import com.onlineexam.dao.ExamQuestionDAO;
import com.onlineexam.model.Exam;
import com.onlineexam.model.ExamAttempt;
import com.onlineexam.model.Question;
import com.onlineexam.service.SubmissionService;
import com.onlineexam.util.ValidationUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Receives the submitted paper (POST only) and finishes the attempt.
 *
 * <p>Steps, all server-side:</p>
 * <ol>
 *   <li>Re-load the attempt and confirm it belongs to this student (anti-IDOR).</li>
 *   <li>If it is already SUBMITTED, just redirect to the result — this makes a
 *       double-submit (e.g. the JS auto-submit racing the manual click) harmless.</li>
 *   <li>Read one answer per <em>allocated</em> question (ignoring any stray or
 *       tampered fields), validating each value is A/B/C/D.</li>
 *   <li>Hand off to {@link SubmissionService} which grades against the DB's correct
 *       answers and writes answers + result + SUBMITTED status in one transaction.</li>
 * </ol>
 *
 * <p>Then Post/Redirect/Get to the result page so a refresh can't resubmit.</p>
 */
@WebServlet("/student/submit-exam")
public class SubmitExamServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ExamDAO examDAO = new ExamDAO();
    private final ExamQuestionDAO examQuestionDAO = new ExamQuestionDAO();
    private final AttemptDAO attemptDAO = new AttemptDAO();
    private final SubmissionService submissionService = new SubmissionService();

    /** A stray GET just returns to the dashboard (nothing to submit). */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/student/dashboard");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        int attemptId = ValidationUtil.parseIntOrDefault(request.getParameter("attemptId"), -1);
        String ctx = request.getContextPath();

        ExamAttempt attempt = attemptDAO.findById(attemptId);
        // Ownership check (anti-IDOR).
        if (attempt == null || attempt.getUserId() != userId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "This attempt is not available.");
            return;
        }

        // Idempotent: already submitted (or auto-submitted on timeout) => show result.
        if (attempt.isSubmitted()) {
            response.sendRedirect(ctx + "/student/result?attemptId=" + attemptId);
            return;
        }

        Exam exam = examDAO.findById(attempt.getExamId());
        if (exam == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Exam not found.");
            return;
        }

        // Collect a chosen option ONLY for questions truly allocated to this exam.
        // Anything else in the request is ignored; invalid values are treated as blank.
        Map<Integer, String> selected = new HashMap<>();
        for (Question q : examQuestionDAO.findQuestionsForExam(exam.getExamId())) {
            String opt = request.getParameter("q_" + q.getQuestionId());
            if (isOption(opt)) {
                selected.put(q.getQuestionId(), opt);
            }
        }

        // Grade + persist atomically (answers, result, SUBMITTED status).
        submissionService.gradeAndPersist(attempt, exam, selected);

        response.sendRedirect(ctx + "/student/result?attemptId=" + attemptId);
    }

    private static boolean isOption(String s) {
        return "A".equals(s) || "B".equals(s) || "C".equals(s) || "D".equals(s);
    }
}
