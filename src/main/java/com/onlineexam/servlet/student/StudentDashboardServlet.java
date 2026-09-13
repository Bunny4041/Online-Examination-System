package com.onlineexam.servlet.student;

import java.io.IOException;
import java.time.LocalDateTime;

import com.onlineexam.dao.AttemptDAO;
import com.onlineexam.dao.ExamDAO;
import com.onlineexam.dao.ResultDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Student landing page. Shows a few at-a-glance numbers (exams available right now,
 * attempts taken, results published) and links into the rest of the student area.
 * The AuthorizationFilter has already guaranteed a logged-in STUDENT reached here,
 * so the session's {@code userId} is always present.
 */
@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ExamDAO examDAO = new ExamDAO();
    private final ResultDAO resultDAO = new ResultDAO();
    private final AttemptDAO attemptDAO = new AttemptDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");

        request.setAttribute("availableCount",
                examDAO.findAvailableForStudents(LocalDateTime.now()).size());
        request.setAttribute("resultCount", resultDAO.findByUser(userId).size());
        request.setAttribute("attemptCount", attemptDAO.findByUser(userId).size());

        request.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp")
               .forward(request, response);
    }
}
