<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Question bank" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h3 mb-0">Question bank</h1>
  <a class="btn btn-primary" href="${ctx}/admin/questions?action=add">Add question</a>
</div>

<form class="row g-2 mb-3" action="${ctx}/admin/questions" method="get">
  <div class="col-sm-8 col-md-6">
    <input type="text" class="form-control" name="q" placeholder="Search question text"
           value="<c:out value='${q}'/>">
  </div>
  <div class="col-auto">
    <button class="btn btn-primary" type="submit">Search</button>
    <a class="btn btn-outline-secondary" href="${ctx}/admin/questions">Clear</a>
  </div>
</form>

<div class="card border-0 shadow-sm">
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead class="table-light">
        <tr><th>#</th><th>Question</th><th>Correct</th><th>Marks</th><th class="text-end">Actions</th></tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${empty questions}">
            <tr><td colspan="5" class="text-center text-muted py-4">No questions found.</td></tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="qn" items="${questions}">
              <tr>
                <td>${qn.questionId}</td>
                <td><c:out value="${qn.questionText}"/></td>
                <td><span class="badge text-bg-info">${qn.correctOption}</span></td>
                <td>${qn.marks}</td>
                <td class="text-end" style="white-space:nowrap">
                  <a class="btn btn-sm btn-outline-secondary"
                     href="${ctx}/admin/questions?action=edit&id=${qn.questionId}">Edit</a>
                  <form action="${ctx}/admin/questions" method="post" class="d-inline">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${qn.questionId}">
                    <button class="btn btn-sm btn-outline-danger"
                            onclick="return confirm('Remove this question from the bank?');">Delete</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>
</div>
<p class="text-muted small mt-2">
  Deleting marks a question inactive so it disappears from the bank, without
  affecting exams or results that already used it.
</p>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
