<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Exams" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h3 mb-0">Exams</h1>
  <a class="btn btn-primary" href="${ctx}/admin/exams?action=add">Create exam</a>
</div>

<c:if test="${notice == 'nopublish'}">
  <div class="alert alert-warning" role="alert">
    You must allocate at least one question to an exam before it can be published.
  </div>
</c:if>

<div class="card border-0 shadow-sm">
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead class="table-light">
        <tr>
          <th>#</th><th>Name</th><th>Window</th><th>Duration</th>
          <th>Questions</th><th>Marks</th><th>Status</th><th class="text-end">Actions</th>
        </tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${empty exams}">
            <tr><td colspan="8" class="text-center text-muted py-4">No exams yet. Create your first one.</td></tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="e" items="${exams}">
              <tr>
                <td>${e.examId}</td>
                <td>
                  <div class="fw-semibold"><c:out value="${e.examName}"/></div>
                  <div class="small text-muted"><c:out value="${e.description}"/></div>
                </td>
                <td class="small">
                  ${e.startDateFormatted}<br>&rarr; ${e.endDateFormatted}
                </td>
                <td>${e.durationMin} min</td>
                <td>
                  <span class="badge text-bg-light border">${e.questionCount}</span>
                  <c:if test="${e.allocatedMarks ne e.maxMarks}">
                    <span class="small text-muted d-block">(${e.allocatedMarks} allocated)</span>
                  </c:if>
                </td>
                <td>pass ${e.passingMarks}/${e.maxMarks}</td>
                <td>
                  <c:choose>
                    <c:when test="${e.published}"><span class="badge text-bg-success">Published</span></c:when>
                    <c:when test="${e.draft}"><span class="badge text-bg-secondary">Draft</span></c:when>
                    <c:otherwise><span class="badge text-bg-dark">Deactivated</span></c:otherwise>
                  </c:choose>
                </td>
                <td class="text-end" style="white-space:nowrap">
                  <a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/allocate?examId=${e.examId}">Questions</a>
                  <c:if test="${not e.deactivated}">
                    <a class="btn btn-sm btn-outline-secondary" href="${ctx}/admin/exams?action=edit&id=${e.examId}">Edit</a>
                  </c:if>
                  <c:if test="${e.draft}">
                    <form action="${ctx}/admin/exams" method="post" class="d-inline">
                      <input type="hidden" name="action" value="publish">
                      <input type="hidden" name="id" value="${e.examId}">
                      <button class="btn btn-sm btn-success">Publish</button>
                    </form>
                  </c:if>
                  <c:if test="${e.published}">
                    <form action="${ctx}/admin/exams" method="post" class="d-inline">
                      <input type="hidden" name="action" value="deactivate">
                      <input type="hidden" name="id" value="${e.examId}">
                      <button class="btn btn-sm btn-outline-danger"
                              onclick="return confirm('Deactivate this exam? Students will no longer see it.');">Deactivate</button>
                    </form>
                  </c:if>
                </td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
