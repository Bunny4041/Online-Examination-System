<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Exam history" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 class="h3 mb-3">Exam history</h1>

<div class="card border-0 shadow-sm">
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead class="table-light">
        <tr>
          <th>Exam</th><th>Started</th><th>Submitted</th>
          <th>Status</th><th class="text-end">Result</th>
        </tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${empty attempts}">
            <tr><td colspan="5" class="text-center text-muted py-4">
              You haven't started any exams yet.
            </td></tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="a" items="${attempts}">
              <tr>
                <td><c:out value="${a.examName}"/></td>
                <td>${a.startTimeFormatted}</td>
                <td>${a.submitTimeFormatted}</td>
                <td>
                  <c:choose>
                    <c:when test="${a.submitted}">
                      <span class="badge text-bg-success">Submitted</span>
                    </c:when>
                    <c:otherwise>
                      <span class="badge text-bg-warning">In progress</span>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td class="text-end">
                  <c:choose>
                    <c:when test="${a.submitted}">
                      <a class="btn btn-sm btn-outline-secondary"
                         href="${ctx}/student/result?attemptId=${a.attemptId}">View result</a>
                    </c:when>
                    <c:otherwise>
                      <a class="btn btn-sm btn-primary"
                         href="${ctx}/student/take-exam?attemptId=${a.attemptId}">Resume</a>
                    </c:otherwise>
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

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
