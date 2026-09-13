<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Your result" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="card border-0 shadow-sm mx-auto" style="max-width:640px">
  <div class="card-body p-4 text-center">
    <h1 class="h4 mb-1"><c:out value="${result.examName}"/></h1>
    <p class="text-muted mb-4">Result</p>

    <c:choose>
      <c:when test="${result.pass}">
        <div class="display-6 fw-bold text-success mb-2">PASS</div>
      </c:when>
      <c:otherwise>
        <div class="display-6 fw-bold text-danger mb-2">FAIL</div>
      </c:otherwise>
    </c:choose>

    <div class="row g-3 my-3">
      <div class="col-4">
        <div class="border rounded p-3">
          <div class="h4 mb-0">${result.marksObtained}</div>
          <div class="text-muted small">Marks scored</div>
        </div>
      </div>
      <div class="col-4">
        <div class="border rounded p-3">
          <div class="h4 mb-0">${result.maxMarks}</div>
          <div class="text-muted small">Out of</div>
        </div>
      </div>
      <div class="col-4">
        <div class="border rounded p-3">
          <div class="h4 mb-0">${result.percentage}%</div>
          <div class="text-muted small">Percentage</div>
        </div>
      </div>
    </div>

    <p class="text-muted small">Completed on ${result.resultDateFormatted}</p>

    <div class="mt-3">
      <a class="btn btn-primary" href="${ctx}/student/my-results">All my results</a>
      <a class="btn btn-outline-secondary" href="${ctx}/student/dashboard">Dashboard</a>
    </div>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
