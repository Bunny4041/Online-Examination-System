package com.onlineexam.servlet.student;

import java.io.IOException;

import com.onlineexam.dao.ResultDAO;
import com.onlineexam.model.Result;
import com.onlineexam.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Shows the scorecard for a single attempt. Ownership is enforced (anti-IDOR):
 * a student can only view their own result, even if they guess another attempt id.
 */
@WebServlet("/student/result")
public class ResultServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ResultDAO resultDAO = new ResultDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        int attemptId = ValidationUtil.parseIntOrDefault(request.getParameter("attemptId"), -1);

        Result result = resultDAO.findByAttempt(attemptId);
        if (result == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Result not found.");
            return;
        }
        if (result.getUserId() != userId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "This result is not available.");
            return;
        }

        request.setAttribute("result", result);
        request.getRequestDispatcher("/WEB-INF/views/student/result.jsp")
               .forward(request, response);
    }
}
