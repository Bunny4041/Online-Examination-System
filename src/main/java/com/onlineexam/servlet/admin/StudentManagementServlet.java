package com.onlineexam.servlet.admin;

import java.io.IOException;
import java.util.List;

import com.onlineexam.dao.AttemptDAO;
import com.onlineexam.dao.ResultDAO;
import com.onlineexam.dao.UserDAO;
import com.onlineexam.model.User;
import com.onlineexam.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Student management for admins.
 * <ul>
 *   <li>GET (default): list or search students.</li>
 *   <li>GET {@code ?action=view&id=..}: one student's profile + their attempts/results.</li>
 *   <li>POST {@code action=activate|deactivate}: toggle a student's account status.</li>
 * </ul>
 * Writes use Post/Redirect/Get so a refresh never repeats the action.
 */
@WebServlet("/admin/students")
public class StudentManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();
    private final AttemptDAO attemptDAO = new AttemptDAO();
    private final ResultDAO resultDAO = new ResultDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("view".equals(action)) {
            int id = ValidationUtil.parseIntOrDefault(request.getParameter("id"), -1);
            User student = userDAO.findById(id);
            if (student == null || !"STUDENT".equals(student.getRole())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found.");
                return;
            }
            request.setAttribute("student", student);
            request.setAttribute("attempts", attemptDAO.findByUser(id));
            request.setAttribute("results", resultDAO.findByUser(id));
            request.getRequestDispatcher("/WEB-INF/views/admin/student-details.jsp")
                   .forward(request, response);
            return;
        }

        String q = request.getParameter("q");
        List<User> students = (q == null || q.isBlank())
                ? userDAO.findAllStudents()
                : userDAO.searchStudents(q.trim());
        request.setAttribute("students", students);
        request.setAttribute("q", q);
        request.getRequestDispatcher("/WEB-INF/views/admin/students.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String action = request.getParameter("action");
        int id = ValidationUtil.parseIntOrDefault(request.getParameter("id"), -1);

        if (id > 0 && "activate".equals(action)) {
            userDAO.updateStatus(id, "ACTIVE");
        } else if (id > 0 && "deactivate".equals(action)) {
            userDAO.updateStatus(id, "INACTIVE");
        }
        response.sendRedirect(request.getContextPath() + "/admin/students");
    }
}
