package com.onlineexam.servlet.student;

import java.io.IOException;

import com.onlineexam.dao.ResultDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Lists all of the logged-in student's results (newest first). The DAO query is
 * already scoped to this user id, so there is no way to see anyone else's scores.
 */
@WebServlet("/student/my-results")
public class MyResultsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ResultDAO resultDAO = new ResultDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        request.setAttribute("results", resultDAO.findByUser(userId));
        request.getRequestDispatcher("/WEB-INF/views/student/my-results.jsp")
               .forward(request, response);
    }
}
