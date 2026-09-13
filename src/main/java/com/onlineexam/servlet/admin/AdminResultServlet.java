package com.onlineexam.servlet.admin;

import java.io.IOException;
import java.util.List;

import com.onlineexam.dao.ResultDAO;
import com.onlineexam.model.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Read-only view of every student's results, with an optional keyword filter
 * (student name/username or exam name).
 */
@WebServlet("/admin/results")
public class AdminResultServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ResultDAO resultDAO = new ResultDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String q = request.getParameter("q");
        List<Result> results = (q == null || q.isBlank())
                ? resultDAO.findAll()
                : resultDAO.search(q.trim());
        request.setAttribute("results", results);
        request.setAttribute("q", q);
        request.getRequestDispatcher("/WEB-INF/views/admin/results.jsp").forward(request, response);
    }
}
