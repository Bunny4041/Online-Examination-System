package com.onlineexam.servlet.student;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.onlineexam.dao.AttemptDAO;
import com.onlineexam.dao.ExamDAO;
import com.onlineexam.dao.ExamQuestionDAO;
import com.onlineexam.model.Exam;
import com.onlineexam.model.ExamAttempt;
import com.onlineexam.model.Question;
import com.onlineexam.service.SubmissionService;
import com.onlineexam.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Renders the live exam paper.
 *
 * <p>Two security properties are enforced here, both on the server:</p>
 * <ol>
 *   <li><b>Ownership (anti-IDOR):</b> the attempt must belong to the logged-in
 *       student, otherwise 403 — you can't view someone else's paper by changing
 *       the {@code attemptId} in the URL.</li>
 *   <li><b>Authoritative timer:</b> the remaining time is recomputed from the
 *       server-stored {@code start_time} on every request, so reloading the page
 *       does not reset the clock and a paused JavaScript timer cannot buy more
 *       time. If the deadline has already passed, the attempt is finalised on the
 *       server (not left hanging) and the student is sent to their result.</li>
 * </ol>
 *
 * <p>The correct answers are stripped via {@link Question#toSafeView()} before the
 * questions ever reach the browser.</p>
 */
@WebServlet("/student/take-exam")
public class TakeExamServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ExamDAO examDAO = new ExamDAO();
    private final ExamQuestionDAO examQuestionDAO = new ExamQuestionDAO();
    private final AttemptDAO attemptDAO = new AttemptDAO();
    private final SubmissionService submissionService = new SubmissionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        int attemptId = ValidationUtil.parseIntOrDefault(request.getParameter("attemptId"), -1);
        String ctx = request.getContextPath();

        ExamAttempt attempt = attemptDAO.findById(attemptId);
        // Ownership check: null OR not-mine => forbidden (don't reveal which).
        if (attempt == null || attempt.getUserId() != userId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "This attempt is not available.");
            return;
        }

        // Already finished? Go straight to the result (idempotent).
        if (attempt.isSubmitted()) {
            response.sendRedirect(ctx + "/student/result?attemptId=" + attemptId);
            return;
        }

        Exam exam = examDAO.findById(attempt.getExamId());
        if (exam == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Exam not found.");
            return;
        }

        // Server-authoritative time remaining.
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = attempt.getStartTime().plusMinutes(exam.getDurationMin());
        long remainingSeconds = Duration.between(now, deadline).getSeconds();

        if (remainingSeconds <= 0) {
            // Time is already up (student left the tab open or never submitted).
            // Finalise on the server with whatever was saved — nothing, in this
            // single-submit design — so the attempt can't sit IN_PROGRESS forever.
            submissionService.gradeAndPersist(attempt, exam, Collections.<Integer, String>emptyMap());
            response.sendRedirect(ctx + "/student/result?attemptId=" + attemptId);
            return;
        }

        // Strip correct answers before sending questions to the browser.
        List<Question> safeQuestions = new ArrayList<>();
        for (Question q : examQuestionDAO.findQuestionsForExam(exam.getExamId())) {
            safeQuestions.add(q.toSafeView());
        }

        request.setAttribute("exam", exam);
        request.setAttribute("attempt", attempt);
        request.setAttribute("questions", safeQuestions);
        request.setAttribute("remainingSeconds", remainingSeconds);

        request.getRequestDispatcher("/WEB-INF/views/student/take-exam.jsp")
               .forward(request, response);
    }
}
