package com.onlineexam.servlet.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.onlineexam.dao.QuestionDAO;
import com.onlineexam.model.Question;
import com.onlineexam.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Question-bank management for admins: list/search, create, edit, soft-delete.
 * "Delete" never removes a row (that would break historical answers/results); it
 * flips the question to INACTIVE via {@link QuestionDAO#softDelete(int)}.
 */
@WebServlet("/admin/questions")
public class QuestionManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final QuestionDAO questionDAO = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            forward(request, response, "/WEB-INF/views/admin/add-question.jsp");
            return;
        }

        if ("edit".equals(action)) {
            int id = ValidationUtil.parseIntOrDefault(request.getParameter("id"), -1);
            Question question = questionDAO.findById(id);
            if (question == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Question not found.");
                return;
            }
            request.setAttribute("question", question);
            forward(request, response, "/WEB-INF/views/admin/edit-question.jsp");
            return;
        }

        String q = request.getParameter("q");
        List<Question> questions = (q == null || q.isBlank())
                ? questionDAO.findAllActive()
                : questionDAO.search(q.trim());
        request.setAttribute("questions", questions);
        request.setAttribute("q", q);
        forward(request, response, "/WEB-INF/views/admin/questions.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String ctx = request.getContextPath();

        if ("delete".equals(action)) {
            int id = ValidationUtil.parseIntOrDefault(request.getParameter("id"), -1);
            if (id > 0) {
                questionDAO.softDelete(id);
            }
            response.sendRedirect(ctx + "/admin/questions");
            return;
        }

        boolean isUpdate = "update".equals(action);
        Question question = buildQuestionFromParams(request, isUpdate);
        List<String> errors = validateQuestion(question);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("question", question);
            forward(request, response, isUpdate
                    ? "/WEB-INF/views/admin/edit-question.jsp"
                    : "/WEB-INF/views/admin/add-question.jsp");
            return;
        }

        if (isUpdate) {
            questionDAO.update(question);
        } else {
            question.setStatus("ACTIVE");
            questionDAO.insert(question);
        }
        response.sendRedirect(ctx + "/admin/questions");
    }

    private Question buildQuestionFromParams(HttpServletRequest request, boolean withId) {
        Question q = new Question();
        if (withId) {
            q.setQuestionId(ValidationUtil.parseIntOrDefault(request.getParameter("questionId"), -1));
        }
        q.setQuestionText(trim(request.getParameter("questionText")));
        q.setOptionA(trim(request.getParameter("optionA")));
        q.setOptionB(trim(request.getParameter("optionB")));
        q.setOptionC(trim(request.getParameter("optionC")));
        q.setOptionD(trim(request.getParameter("optionD")));
        String correct = trim(request.getParameter("correctOption"));
        q.setCorrectOption(correct == null ? null : correct.toUpperCase());
        q.setMarks(ValidationUtil.parseIntOrDefault(request.getParameter("marks"), 0));
        return q;
    }

    private List<String> validateQuestion(Question q) {
        List<String> errors = new ArrayList<>();
        if (ValidationUtil.isBlank(q.getQuestionText())) {
            errors.add("Question text is required.");
        }
        if (ValidationUtil.isBlank(q.getOptionA()) || ValidationUtil.isBlank(q.getOptionB())
                || ValidationUtil.isBlank(q.getOptionC()) || ValidationUtil.isBlank(q.getOptionD())) {
            errors.add("All four options (A, B, C, D) are required.");
        }
        if (!isOption(q.getCorrectOption())) {
            errors.add("Please choose which option (A/B/C/D) is correct.");
        }
        if (!ValidationUtil.isPositiveInt(q.getMarks())) {
            errors.add("Marks must be greater than 0.");
        }
        return errors;
    }

    private static boolean isOption(String s) {
        return "A".equals(s) || "B".equals(s) || "C".equals(s) || "D".equals(s);
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        request.getRequestDispatcher(path).forward(request, response);
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
