package com.onlineexam.servlet.auth;

import java.io.IOException;

import com.onlineexam.dao.UserDAO;
import com.onlineexam.model.User;
import com.onlineexam.util.PasswordUtil;
import com.onlineexam.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Login. GET shows the form; POST authenticates and, on success, creates a fresh
 * session (session-fixation defence) storing only {@code userId}, {@code username}
 * and {@code role}, then routes to the role's dashboard.
 *
 * <p>The error message is deliberately generic ("Invalid username or password") so
 * it never reveals which field was wrong.</p>
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If already logged in, skip the form and go to the right dashboard.
        HttpSession existing = request.getSession(false);
        if (existing != null && existing.getAttribute("userId") != null) {
            redirectByRole(request, response, (String) existing.getAttribute("role"));
            return;
        }
        forwardToForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = trim(request.getParameter("username"));
        String password = request.getParameter("password");

        if (ValidationUtil.isBlank(username) || ValidationUtil.isBlank(password)) {
            fail(request, response, username, "Please enter both username and password.");
            return;
        }

        User user = userDAO.findByUsername(username);
        boolean credentialsOk = user != null && PasswordUtil.verify(password, user.getPasswordHash());
        if (!credentialsOk) {
            fail(request, response, username, "Invalid username or password.");
            return;
        }
        if (!user.isActive()) {
            fail(request, response, username,
                    "Your account is deactivated. Please contact the administrator.");
            return;
        }

        // Session-fixation defence: drop any pre-login session, start a clean one.
        HttpSession old = request.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        redirectByRole(request, response, user.getRole());
    }

    private void redirectByRole(HttpServletRequest request, HttpServletResponse response,
                                String role) throws IOException {
        String target = "ADMIN".equals(role) ? "/admin/dashboard" : "/student/dashboard";
        response.sendRedirect(request.getContextPath() + target);
    }

    private void fail(HttpServletRequest request, HttpServletResponse response,
                      String username, String message) throws ServletException, IOException {
        request.setAttribute("error", message);
        request.setAttribute("username", username); // sticky username, never password
        forwardToForm(request, response);
    }

    private void forwardToForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
