package com.onlineexam.servlet.admin;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.onlineexam.dao.ExamDAO;
import com.onlineexam.dao.ExamQuestionDAO;
import com.onlineexam.model.Exam;
import com.onlineexam.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Exam lifecycle management for admins: list, create, edit, publish, deactivate.
 * All writes re-validate on the server through {@link ValidationUtil} and use
 * Post/Redirect/Get. Publishing is blocked unless the exam has at least one
 * allocated question.
 */
@WebServlet("/admin/exams")
public class ExamManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ExamDAO examDAO = new ExamDAO();
    private final ExamQuestionDAO examQuestionDAO = new ExamQuestionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            forward(request, response, "/WEB-INF/views/admin/add-exam.jsp");
            return;
        }

        if ("edit".equals(action)) {
            int id = ValidationUtil.parseIntOrDefault(request.getParameter("id"), -1);
            Exam exam = examDAO.findById(id);
            if (exam == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Exam not found.");
                return;
            }
            request.setAttribute("exam", exam);
            forward(request, response, "/WEB-INF/views/admin/edit-exam.jsp");
            return;
        }

        // Default: list all exams with question counts / allocated marks.
        List<Exam> exams = examDAO.findAll();
        for (Exam e : exams) {
            e.setQuestionCount(examQuestionDAO.countByExam(e.getExamId()));
            e.setAllocatedMarks(examQuestionDAO.sumMarksByExam(e.getExamId()));
        }
        request.setAttribute("exams", exams);
        request.setAttribute("notice", request.getParameter("notice"));
        forward(request, response, "/WEB-INF/views/admin/exams.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String ctx = request.getContextPath();

        if ("publish".equals(action)) {
            int id = ValidationUtil.parseIntOrDefault(request.getParameter("id"), -1);
            if (id > 0 && examQuestionDAO.countByExam(id) > 0) {
                examDAO.updateStatus(id, "PUBLISHED");
                response.sendRedirect(ctx + "/admin/exams");
            } else {
                // Can't publish an exam with no questions.
                response.sendRedirect(ctx + "/admin/exams?notice=nopublish");
            }
            return;
        }

        if ("deactivate".equals(action)) {
            int id = ValidationUtil.parseIntOrDefault(request.getParameter("id"), -1);
            if (id > 0) {
                examDAO.updateStatus(id, "DEACTIVATED");
            }
            response.sendRedirect(ctx + "/admin/exams");
            return;
        }

        boolean isUpdate = "update".equals(action);
        Exam exam = buildExamFromParams(request, isUpdate);
        List<String> errors = validateExam(exam);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("exam", exam);
            forward(request, response, isUpdate
                    ? "/WEB-INF/views/admin/edit-exam.jsp"
                    : "/WEB-INF/views/admin/add-exam.jsp");
            return;
        }

        if (isUpdate) {
            examDAO.update(exam);
        } else {
            HttpSession session = request.getSession(false);
            Integer adminId = session == null ? null : (Integer) session.getAttribute("userId");
            exam.setCreatedBy(adminId == null ? 0 : adminId);
            exam.setStatus("DRAFT");
            examDAO.insert(exam);
        }
        response.sendRedirect(ctx + "/admin/exams");
    }

    private Exam buildExamFromParams(HttpServletRequest request, boolean withId) {
        Exam e = new Exam();
        if (withId) {
            e.setExamId(ValidationUtil.parseIntOrDefault(request.getParameter("examId"), -1));
        }
        e.setExamName(trim(request.getParameter("examName")));
        e.setDescription(trim(request.getParameter("description")));
        e.setDurationMin(ValidationUtil.parseIntOrDefault(request.getParameter("durationMin"), 0));
        e.setMaxMarks(ValidationUtil.parseIntOrDefault(request.getParameter("maxMarks"), 0));
        e.setPassingMarks(ValidationUtil.parseIntOrDefault(request.getParameter("passingMarks"), -1));
        e.setStartDate(parseDateTimeLocal(request.getParameter("startDate")));
        e.setEndDate(parseDateTimeLocal(request.getParameter("endDate")));
        return e;
    }

    private List<String> validateExam(Exam e) {
        List<String> errors = new ArrayList<>();
        if (ValidationUtil.isBlank(e.getExamName())) {
            errors.add("Exam name is required.");
        }
        if (!ValidationUtil.isPositiveInt(e.getDurationMin())) {
            errors.add("Duration (minutes) must be greater than 0.");
        }
        if (!ValidationUtil.isPositiveInt(e.getMaxMarks())) {
            errors.add("Maximum marks must be greater than 0.");
        }
        if (!ValidationUtil.marksValid(e.getPassingMarks(), e.getMaxMarks())) {
            errors.add("Passing marks must be between 0 and the maximum marks.");
        }
        if (!ValidationUtil.datesValid(e.getStartDate(), e.getEndDate())) {
            errors.add("Please provide a valid start and end date (end must not be before start).");
        }
        return errors;
    }

    private static LocalDateTime parseDateTimeLocal(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            // HTML datetime-local => "yyyy-MM-ddTHH:mm" (ISO local date-time)
            return LocalDateTime.parse(s.trim());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        request.getRequestDispatcher(path).forward(request, response);
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
