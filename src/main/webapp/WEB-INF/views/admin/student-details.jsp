<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Student details" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<nav aria-label="breadcrumb">
  <ol class="breadcrumb">
    <li class="breadcrumb-item"><a href="${ctx}/admin/students">Students</a></li>
    <li class="breadcrumb-item active" aria-current="page"><c:out value="${student.fullName}"/></li>
  </ol>
</nav>

<div class="card border-0 shadow-sm mb-4">
  <div class="card-body">
    <h1 class="h4"><c:out value="${student.fullName}"/></h1>
    <div class="row text-muted">
      <div class="col-md-4">Username: <span class="text-body"><c:out value="${student.username}"/></span></div>
      <div class="col-md-4">Email: <span class="text-body"><c:out value="${student.email}"/></span></div>
      <div class="col-md-4">
        Status:
        <c:choose>
          <c:when test="${student.active}"><span class="badge text-bg-success">Active</span></c:when>
          <c:otherwise><span class="badge text-bg-secondary">Inactive</span></c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
</div>

<h2 class="h5">Attempts</h2>
<div class="card border-0 shadow-sm mb-4">
  <div class="table-responsive">
    <table class="table mb-0 align-middle">
      <thead class="table-light">
        <tr><th>Exam</th><th>Started</th><th>Submitted</th><th>Status</th></tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${empty attempts}">
            <tr><td colspan="4" class="text-center text-muted py-3">No attempts yet.</td></tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="a" items="${attempts}">
              <tr>
                <td><c:out value="${a.examName}"/></td>
                <td>${a.startTimeFormatted}</td>
                <td>${a.submitTimeFormatted}</td>
                <td>
                  <c:choose>
                    <c:when test="${a.submitted}"><span class="badge text-bg-success">Submitted</span></c:when>
                    <c:otherwise><span class="badge text-bg-warning">In progress</span></c:otherwise>
                  </c:choose>
                </td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>
</div>

<h2 class="h5">Results</h2>
<div class="card border-0 shadow-sm">
  <div class="table-responsive">
    <table class="table mb-0 align-middle">
      <thead class="table-light">
        <tr><th>Exam</th><th>Score</th><th>Percentage</th><th>Result</th><th>Date</th></tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${empty results}">
            <tr><td colspan="5" class="text-center text-muted py-3">No results yet.</td></tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="r" items="${results}">
              <tr>
                <td><c:out value="${r.examName}"/></td>
                <td>${r.marksObtained} / ${r.maxMarks}</td>
                <td>${r.percentage}%</td>
                <td>
                  <c:choose>
                    <c:when test="${r.pass}"><span class="badge text-bg-success">Pass</span></c:when>
                    <c:otherwise><span class="badge text-bg-danger">Fail</span></c:otherwise>
                  </c:choose>
                </td>
                <td>${r.resultDateFormatted}</td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
