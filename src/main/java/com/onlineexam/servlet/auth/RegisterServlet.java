package com.onlineexam.servlet.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.onlineexam.dao.UserDAO;
import com.onlineexam.model.User;
import com.onlineexam.util.PasswordUtil;
import com.onlineexam.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Registration. GET shows the form; POST validates on the server, enforces
 * uniqueness, hashes the password, and creates a STUDENT account.
 *
 * <p>New accounts are always role STUDENT / status ACTIVE — a browser cannot ask
 * to become an admin (the admin account is seeded directly in the database).</p>
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardToForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = trim(request.getParameter("username"));
        String password = request.getParameter("password"); // not trimmed on purpose
        String fullName = trim(request.getParameter("fullName"));
        String email = trim(request.getParameter("email"));

        List<String> errors = new ArrayList<>();

        if (ValidationUtil.isBlank(username) || ValidationUtil.isBlank(password)
                || ValidationUtil.isBlank(fullName) || ValidationUtil.isBlank(email)) {
            errors.add("All fields are required.");
        }
        if (!ValidationUtil.isBlank(username) && !ValidationUtil.isValidUsername(username)) {
            errors.add("Username must be 3-50 characters: letters, digits or underscore.");
        }
        if (!ValidationUtil.isBlank(email) && !ValidationUtil.isValidEmail(email)) {
            errors.add("Please enter a valid email address.");
        }
        if (!ValidationUtil.isBlank(password) && !ValidationUtil.isStrongPassword(password)) {
            errors.add("Password must be at least 6 characters and include a letter and a digit.");
        }
        // Uniqueness checks only if the basic format is OK (avoids noisy DB hits).
        if (ValidationUtil.isValidUsername(username) && userDAO.existsByUsername(username)) {
            errors.add("That username is already taken.");
        }
        if (ValidationUtil.isValidEmail(email) && userDAO.existsByEmail(email)) {
            errors.add("That email is already registered.");
        }

        if (!errors.isEmpty()) {
            // Re-show the form with messages and sticky values (never the password).
            request.setAttribute("errors", errors);
            request.setAttribute("username", username);
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            forwardToForm(request, response);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole("STUDENT");
        user.setStatus("ACTIVE");
        userDAO.insert(user);

        // Post/Redirect/Get: redirect to login with a success flag.
        response.sendRedirect(request.getContextPath() + "/login?registered=1");
    }

    private void forwardToForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
