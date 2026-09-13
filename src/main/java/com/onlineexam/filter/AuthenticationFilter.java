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
 * Guards {@code /admin/*} and {@code /student/*}: if the current session has no
 * logged-in user, the request is bounced to the login page. This runs BEFORE
 * {@link AuthorizationFilter} (order is fixed by the {@code <filter-mapping>}
 * sequence in web.xml), so by the time authorization runs, a user is guaranteed
 * to be present.
 *
 * <p>Declared in web.xml (not via {@code @WebFilter}) because annotation-based
 * filter ordering is not guaranteed by the servlet spec.</p>
 */
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // getSession(false) => do NOT create a session just to check for one.
        HttpSession session = req.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("userId") != null;

        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
            // Not authenticated: send them to the login page.
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
