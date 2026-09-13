<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Edit question" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<nav aria-label="breadcrumb">
  <ol class="breadcrumb">
    <li class="breadcrumb-item"><a href="${ctx}/admin/questions">Question bank</a></li>
    <li class="breadcrumb-item active" aria-current="page">Edit question</li>
  </ol>
</nav>

<div class="card border-0 shadow-sm">
  <div class="card-body p-4">
    <h1 class="h4 mb-3">Edit question</h1>

    <c:if test="${not empty errors}">
      <div class="alert alert-danger"><ul class="mb-0">
        <c:forEach var="e" items="${errors}"><li><c:out value="${e}"/></li></c:forEach>
      </ul></div>
    </c:if>

    <form action="${ctx}/admin/questions" method="post" novalidate>
      <input type="hidden" name="action" value="update">
      <input type="hidden" name="questionId" value="${question.questionId}">

      <div class="mb-3">
        <label class="form-label" for="questionText">Question</label>
        <textarea class="form-control" id="questionText" name="questionText" rows="2" required><c:out value="${question.questionText}"/></textarea>
      </div>

      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label" for="optionA">Option A</label>
          <input type="text" class="form-control" id="optionA" name="optionA"
                 value="<c:out value='${question.optionA}'/>" required>
        </div>
        <div class="col-md-6">
          <label class="form-label" for="optionB">Option B</label>
          <input type="text" class="form-control" id="optionB" name="optionB"
                 value="<c:out value='${question.optionB}'/>" required>
        </div>
        <div class="col-md-6">
          <label class="form-label" for="optionC">Option C</label>
          <input type="text" class="form-control" id="optionC" name="optionC"
                 value="<c:out value='${question.optionC}'/>" required>
        </div>
        <div class="col-md-6">
          <label class="form-label" for="optionD">Option D</label>
          <input type="text" class="form-control" id="optionD" name="optionD"
                 value="<c:out value='${question.optionD}'/>" required>
        </div>
      </div>

      <div class="row g-3 mt-0">
        <div class="col-md-4">
          <label class="form-label" for="correctOption">Correct option</label>
          <select class="form-select" id="correctOption" name="correctOption" required>
            <option value="">Choose…</option>
            <option value="A" ${question.correctOption == 'A' ? 'selected' : ''}>A</option>
            <option value="B" ${question.correctOption == 'B' ? 'selected' : ''}>B</option>
            <option value="C" ${question.correctOption == 'C' ? 'selected' : ''}>C</option>
            <option value="D" ${question.correctOption == 'D' ? 'selected' : ''}>D</option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label" for="marks">Marks</label>
          <input type="number" min="1" class="form-control" id="marks" name="marks"
                 value="${question.marks}" required>
        </div>
      </div>

      <div class="mt-4">
        <button type="submit" class="btn btn-primary">Save changes</button>
        <a class="btn btn-outline-secondary" href="${ctx}/admin/questions">Cancel</a>
      </div>
    </form>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
