package com.onlineexam.servlet.student;

import java.io.IOException;
import java.time.LocalDateTime;

import com.onlineexam.dao.AttemptDAO;
import com.onlineexam.dao.ExamDAO;
import com.onlineexam.dao.ExamQuestionDAO;
import com.onlineexam.model.Exam;
import com.onlineexam.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Read-only "cover page" for one exam: duration, marks, window, number of questions,
 * and whether this student may start. The page decides what to show from the flags
 * computed here; the actual eligibility is re-checked on the server when the student
 * presses Start (see {@link StartExamServlet}), so this is purely informational.
 *
 * <p>Only PUBLISHED exams are viewable — a draft/deactivated exam returns 404 so
 * students can't probe for unpublished content by guessing ids.</p>
 */
@WebServlet("/student/exam-details")
public class ExamDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ExamDAO examDAO = new ExamDAO();
    private final ExamQuestionDAO examQuestionDAO = new ExamQuestionDAO();
    private final AttemptDAO attemptDAO = new AttemptDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        int examId = ValidationUtil.parseIntOrDefault(request.getParameter("examId"), -1);

        Exam exam = examDAO.findById(examId);
        if (exam == null || !exam.isPublished()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Exam not found.");
            return;
        }

        int questionCount = examQuestionDAO.countByExam(examId);
        boolean attempted = attemptDAO.hasAttempted(userId, examId);

        LocalDateTime now = LocalDateTime.now();
        boolean open = exam.getStartDate() != null && exam.getEndDate() != null
                && !now.isBefore(exam.getStartDate())
                && !now.isAfter(exam.getEndDate());

        // Eligible = window open, has questions, and not already attempted.
        boolean eligible = open && questionCount > 0 && !attempted;

        request.setAttribute("exam", exam);
        request.setAttribute("questionCount", questionCount);
        request.setAttribute("allocatedMarks", examQuestionDAO.sumMarksByExam(examId));
        request.setAttribute("attempted", attempted);
        request.setAttribute("open", open);
        request.setAttribute("eligible", eligible);
        request.setAttribute("notice", request.getParameter("notice"));

        request.getRequestDispatcher("/WEB-INF/views/student/exam-details.jsp")
               .forward(request, response);
    }
}
