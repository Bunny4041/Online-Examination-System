package com.onlineexam.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Enforces role-based access on the two protected areas:
 * <ul>
 *   <li>{@code /admin/*}   → requires role ADMIN</li>
 *   <li>{@code /student/*} → requires role STUDENT</li>
 * </ul>
 * Runs AFTER {@link AuthenticationFilter}, so a logged-in user is guaranteed; here
 * we only check that their role matches the area. A mismatch (e.g. a student typing
 * {@code /admin/exams} into the address bar) yields HTTP 403, which web.xml maps to
 * access-denied.jsp. This — not the hidden UI links — is the real access guard.
 */
public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        String role = (session == null) ? null : (String) session.getAttribute("role");

        // Path within the app, e.g. "/admin/exams" (strip the context path).
        String path = req.getRequestURI().substring(req.getContextPath().length());

        boolean allowed;
        if (path.startsWith("/admin/")) {
            allowed = "ADMIN".equals(role);
        } else if (path.startsWith("/student/")) {
            allowed = "STUDENT".equals(role);
        } else {
            // Not a guarded area (shouldn't happen given the mappings) — let it pass.
            allowed = true;
        }

        if (allowed) {
            chain.doFilter(request, response);
        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "You do not have permission to access this resource.");
        }
    }
}
