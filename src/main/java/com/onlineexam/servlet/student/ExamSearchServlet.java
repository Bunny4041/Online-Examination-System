package com.onlineexam.servlet.student;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.onlineexam.dao.AttemptDAO;
import com.onlineexam.dao.ExamDAO;
import com.onlineexam.dao.ExamQuestionDAO;
import com.onlineexam.model.Exam;
import com.onlineexam.model.ExamAttempt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Browse / search the exams a student can currently take. The "available" rule
 * (PUBLISHED and inside the start/end window) lives entirely in
 * {@link ExamDAO#findAvailableForStudents(LocalDateTime)}; the optional keyword box
 * just narrows that list in memory so we never accidentally surface a draft or an
 * exam outside its window. Exams the student has already attempted are flagged so
 * the page can disable their "Start" button.
 */
@WebServlet("/student/search")
public class ExamSearchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ExamDAO examDAO = new ExamDAO();
    private final ExamQuestionDAO examQuestionDAO = new ExamQuestionDAO();
    private final AttemptDAO attemptDAO = new AttemptDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String q = request.getParameter("q");

        List<Exam> available = examDAO.findAvailableForStudents(LocalDateTime.now());

        // Optional keyword filter, applied on top of the availability rule.
        if (q != null && !q.isBlank()) {
            String needle = q.trim().toLowerCase();
            List<Exam> filtered = new ArrayList<>();
            for (Exam e : available) {
                if (e.getExamName() != null && e.getExamName().toLowerCase().contains(needle)) {
                    filtered.add(e);
                }
            }
            available = filtered;
        }

        // Show how many questions each exam has (helps the student decide).
        for (Exam e : available) {
            e.setQuestionCount(examQuestionDAO.countByExam(e.getExamId()));
        }

        // Which of these has the student already used up their single attempt on?
        Set<Integer> attemptedExamIds = new HashSet<>();
        for (ExamAttempt a : attemptDAO.findByUser(userId)) {
            attemptedExamIds.add(a.getExamId());
        }

        request.setAttribute("exams", available);
        request.setAttribute("attemptedExamIds", attemptedExamIds);
        request.setAttribute("q", q);
        request.getRequestDispatcher("/WEB-INF/views/student/exams.jsp")
               .forward(request, response);
    }
}
