package com.onlineexam.servlet.student;

import java.io.IOException;
import java.time.LocalDateTime;

import com.onlineexam.dao.AttemptDAO;
import com.onlineexam.dao.ExamDAO;
import com.onlineexam.dao.ExamQuestionDAO;
import com.onlineexam.model.Exam;
import com.onlineexam.model.ExamAttempt;
import com.onlineexam.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Starts an attempt (POST only, so it can't be triggered by a plain link/refresh).
 *
 * <p>Every eligibility rule is re-checked here on the server — never trust the
 * button state from the previous page:</p>
 * <ul>
 *   <li>exam exists, is PUBLISHED, and "now" is inside its window;</li>
 *   <li>the exam actually has questions;</li>
 *   <li>the student has not already attempted it (single-attempt rule, also backed
 *       by a UNIQUE(user_id, exam_id) constraint as a last line of defence).</li>
 * </ul>
 *
 * <p>On success it records {@code start_time} on the server (the timer's anchor) and
 * redirects to the take-exam page (Post/Redirect/Get, so a refresh won't re-insert).</p>
 */
@WebServlet("/student/start-exam")
public class StartExamServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ExamDAO examDAO = new ExamDAO();
    private final ExamQuestionDAO examQuestionDAO = new ExamQuestionDAO();
    private final AttemptDAO attemptDAO = new AttemptDAO();

    /** A stray GET (e.g. someone typing the URL) just goes back to the exam list. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/student/search");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        int examId = ValidationUtil.parseIntOrDefault(request.getParameter("examId"), -1);
        String ctx = request.getContextPath();

        Exam exam = examDAO.findById(examId);
        LocalDateTime now = LocalDateTime.now();

        boolean open = exam != null && exam.isPublished()
                && exam.getStartDate() != null && exam.getEndDate() != null
                && !now.isBefore(exam.getStartDate())
                && !now.isAfter(exam.getEndDate());
        boolean hasQuestions = exam != null && examQuestionDAO.countByExam(examId) > 0;

        if (!open || !hasQuestions || attemptDAO.hasAttempted(userId, examId)) {
            // Not allowed to start — send them back to the details page with a reason.
            response.sendRedirect(ctx + "/student/exam-details?examId=" + examId + "&notice=ineligible");
            return;
        }

        ExamAttempt attempt = new ExamAttempt();
        attempt.setUserId(userId);
        attempt.setExamId(examId);
        attempt.setStartTime(now);          // server-recorded: the timer counts from here
        attempt.setStatus("IN_PROGRESS");
        int attemptId = attemptDAO.insert(attempt);

        response.sendRedirect(ctx + "/student/take-exam?attemptId=" + attemptId);
    }
}
