<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="All results" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 class="h3 mb-3">All results</h1>

<form class="row g-2 mb-3" action="${ctx}/admin/results" method="get">
  <div class="col-sm-8 col-md-6">
    <input type="text" class="form-control" name="q"
           placeholder="Search by student or exam name"
           value="<c:out value='${q}'/>">
  </div>
  <div class="col-auto">
    <button class="btn btn-primary" type="submit">Search</button>
    <a class="btn btn-outline-secondary" href="${ctx}/admin/results">Clear</a>
  </div>
</form>

<div class="card border-0 shadow-sm">
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead class="table-light">
        <tr>
          <th>#</th><th>Student</th><th>Exam</th><th>Score</th>
          <th>Percentage</th><th>Result</th><th>Date</th>
        </tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${empty results}">
            <tr><td colspan="7" class="text-center text-muted py-4">No results found.</td></tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="r" items="${results}">
              <tr>
                <td>${r.resultId}</td>
                <td>
                  <div><c:out value="${r.studentName}"/></div>
                  <span class="text-muted small">@<c:out value="${r.studentUsername}"/></span>
                </td>
                <td><c:out value="${r.examName}"/></td>
                <td>${r.marksObtained} / ${r.maxMarks}</td>
                <td>${r.percentage}%</td>
                <td>
                  <c:choose>
                    <c:when test="${r.pass}"><span class="badge text-bg-success">PASS</span></c:when>
                    <c:otherwise><span class="badge text-bg-danger">FAIL</span></c:otherwise>
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
