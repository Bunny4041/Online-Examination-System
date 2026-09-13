<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My results" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 class="h3 mb-3">My results</h1>

<div class="card border-0 shadow-sm">
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead class="table-light">
        <tr>
          <th>Exam</th><th>Score</th><th>Percentage</th>
          <th>Result</th><th>Date</th><th class="text-end">Details</th>
        </tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${empty results}">
            <tr><td colspan="6" class="text-center text-muted py-4">
              You haven't completed any exams yet.
            </td></tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="r" items="${results}">
              <tr>
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
                <td class="text-end">
                  <a class="btn btn-sm btn-outline-secondary"
                     href="${ctx}/student/result?attemptId=${r.attemptId}">View</a>
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
