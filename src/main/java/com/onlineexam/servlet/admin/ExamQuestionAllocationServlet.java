package com.onlineexam.servlet.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.onlineexam.dao.ExamDAO;
import com.onlineexam.dao.ExamQuestionDAO;
import com.onlineexam.dao.QuestionDAO;
import com.onlineexam.model.Exam;
import com.onlineexam.model.Question;
import com.onlineexam.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Allocate / deallocate questions to an exam.
 * <ul>
 *   <li>GET without examId: shows an exam picker.</li>
 *   <li>GET with examId: shows the exam's allocated questions and the remaining
 *       bank of active questions available to add, plus a running marks total.</li>
 *   <li>POST {@code action=allocate|deallocate}: modify the mapping, then redirect
 *       back to the same exam (PRG).</li>
 * </ul>
 */
@WebServlet("/admin/allocate")
public class ExamQuestionAllocationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ExamDAO examDAO = new ExamDAO();
    private final ExamQuestionDAO examQuestionDAO = new ExamQuestionDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Exam picker is always available.
        request.setAttribute("exams", examDAO.findAll());

        int examId = ValidationUtil.parseIntOrDefault(request.getParameter("examId"), -1);
        if (examId > 0) {
            Exam exam = examDAO.findById(examId);
            if (exam == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Exam not found.");
                return;
            }

            List<Question> allocated = examQuestionDAO.findQuestionsForExam(examId);

            // Available = active questions not already allocated to this exam.
            Set<Integer> allocatedIds = new HashSet<>();
            for (Question q : allocated) {
                allocatedIds.add(q.getQuestionId());
            }
            List<Question> available = new ArrayList<>();
            for (Question q : questionDAO.findAllActive()) {
                if (!allocatedIds.contains(q.getQuestionId())) {
                    available.add(q);
                }
            }

            request.setAttribute("exam", exam);
            request.setAttribute("allocatedQuestions", allocated);
            request.setAttribute("availableQuestions", available);
            request.setAttribute("allocatedMarks", examQuestionDAO.sumMarksByExam(examId));
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/allocate-questions.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String action = request.getParameter("action");
        int examId = ValidationUtil.parseIntOrDefault(request.getParameter("examId"), -1);
        int questionId = ValidationUtil.parseIntOrDefault(request.getParameter("questionId"), -1);
        String ctx = request.getContextPath();

        if (examId > 0 && questionId > 0) {
            if ("allocate".equals(action)) {
                if (!examQuestionDAO.isAllocated(examId, questionId)) {
                    int nextOrder = examQuestionDAO.countByExam(examId) + 1;
                    examQuestionDAO.allocate(examId, questionId, nextOrder);
                }
            } else if ("deallocate".equals(action)) {
                examQuestionDAO.deallocate(examId, questionId);
            }
        }
        response.sendRedirect(ctx + "/admin/allocate?examId=" + examId);
    }
}
