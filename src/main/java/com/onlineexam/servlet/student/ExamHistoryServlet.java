package com.onlineexam.servlet.student;

import java.io.IOException;

import com.onlineexam.dao.AttemptDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Lists all of the logged-in student's attempts (both IN_PROGRESS and SUBMITTED),
 * newest first, with the exam name joined for display. Scoped to this user id.
 */
@WebServlet("/student/history")
public class ExamHistoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final AttemptDAO attemptDAO = new AttemptDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        request.setAttribute("attempts", attemptDAO.findByUser(userId));
        request.getRequestDispatcher("/WEB-INF/views/student/history.jsp")
               .forward(request, response);
    }
}
