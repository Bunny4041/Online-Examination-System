<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Dashboard" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-4">
  <h1 class="h3 mb-0">Admin Dashboard</h1>
  <span class="text-muted">Welcome, <c:out value="${sessionScope.username}"/></span>
</div>

<div class="row g-3 mb-4">
  <div class="col-6 col-lg-3">
    <a class="text-decoration-none" href="${ctx}/admin/students">
      <div class="card border-0 shadow-sm h-100">
        <div class="card-body text-center">
          <div class="display-6 fw-bold text-primary">${studentCount}</div>
          <div class="text-muted">Students</div>
        </div>
      </div>
    </a>
  </div>
  <div class="col-6 col-lg-3">
    <a class="text-decoration-none" href="${ctx}/admin/exams">
      <div class="card border-0 shadow-sm h-100">
        <div class="card-body text-center">
          <div class="display-6 fw-bold text-primary">${examCount}</div>
          <div class="text-muted">Exams</div>
        </div>
      </div>
    </a>
  </div>
  <div class="col-6 col-lg-3">
    <a class="text-decoration-none" href="${ctx}/admin/questions">
      <div class="card border-0 shadow-sm h-100">
        <div class="card-body text-center">
          <div class="display-6 fw-bold text-primary">${questionCount}</div>
          <div class="text-muted">Questions</div>
        </div>
      </div>
    </a>
  </div>
  <div class="col-6 col-lg-3">
    <a class="text-decoration-none" href="${ctx}/admin/results">
      <div class="card border-0 shadow-sm h-100">
        <div class="card-body text-center">
          <div class="display-6 fw-bold text-primary">${resultCount}</div>
          <div class="text-muted">Results</div>
        </div>
      </div>
    </a>
  </div>
</div>

<div class="card border-0 shadow-sm">
  <div class="card-body">
    <h2 class="h5">Quick actions</h2>
    <div class="d-flex flex-wrap gap-2 mt-3">
      <a class="btn btn-primary" href="${ctx}/admin/exams?action=add">Create exam</a>
      <a class="btn btn-outline-primary" href="${ctx}/admin/questions?action=add">Add question</a>
      <a class="btn btn-outline-primary" href="${ctx}/admin/allocate">Allocate questions</a>
      <a class="btn btn-outline-primary" href="${ctx}/admin/results">View results</a>
    </div>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
