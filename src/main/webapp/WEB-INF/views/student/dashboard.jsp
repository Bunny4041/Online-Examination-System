<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Student dashboard" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 class="h3 mb-1">Welcome, <c:out value="${sessionScope.username}"/></h1>
<p class="text-muted">Here's a quick look at your exams and results.</p>

<div class="row g-3 mb-4">
  <div class="col-sm-4">
    <div class="card border-0 shadow-sm h-100">
      <div class="card-body text-center">
        <div class="display-6 fw-bold text-primary">${availableCount}</div>
        <div class="text-muted">Exams available now</div>
      </div>
    </div>
  </div>
  <div class="col-sm-4">
    <div class="card border-0 shadow-sm h-100">
      <div class="card-body text-center">
        <div class="display-6 fw-bold text-success">${resultCount}</div>
        <div class="text-muted">Results published</div>
      </div>
    </div>
  </div>
  <div class="col-sm-4">
    <div class="card border-0 shadow-sm h-100">
      <div class="card-body text-center">
        <div class="display-6 fw-bold text-secondary">${attemptCount}</div>
        <div class="text-muted">Attempts taken</div>
      </div>
    </div>
  </div>
</div>

<div class="d-flex flex-wrap gap-2">
  <a class="btn btn-primary" href="${ctx}/student/search">Browse exams</a>
  <a class="btn btn-outline-secondary" href="${ctx}/student/my-results">My results</a>
  <a class="btn btn-outline-secondary" href="${ctx}/student/history">Exam history</a>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
