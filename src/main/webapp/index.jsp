<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Welcome" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="p-5 mb-4 bg-white rounded-3 shadow-sm border">
  <div class="text-center py-4">
    <h1 class="display-5 fw-bold">Online Examination System</h1>
    <p class="fs-5 text-muted col-lg-8 mx-auto">
      Create exams, manage a question bank, and let students take timed
      multiple-choice tests with instant, automatically graded results.
    </p>

    <div class="mt-4">
      <c:choose>
        <c:when test="${sessionScope.role == 'ADMIN'}">
          <a class="btn btn-primary btn-lg px-4" href="${ctx}/admin/dashboard">Go to Admin Dashboard</a>
        </c:when>
        <c:when test="${sessionScope.role == 'STUDENT'}">
          <a class="btn btn-primary btn-lg px-4" href="${ctx}/student/dashboard">Go to My Dashboard</a>
        </c:when>
        <c:otherwise>
          <a class="btn btn-primary btn-lg px-4 me-2" href="${ctx}/login">Login</a>
          <a class="btn btn-outline-primary btn-lg px-4" href="${ctx}/register">Register</a>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>

<div class="row g-4">
  <div class="col-md-4">
    <div class="card h-100 border-0 shadow-sm">
      <div class="card-body">
        <h5 class="card-title">For Students</h5>
        <p class="card-text text-muted">
          Browse available exams, take them within a fixed time limit, and see your
          score the moment you submit.
        </p>
      </div>
    </div>
  </div>
  <div class="col-md-4">
    <div class="card h-100 border-0 shadow-sm">
      <div class="card-body">
        <h5 class="card-title">For Administrators</h5>
        <p class="card-text text-muted">
          Build a reusable question bank, assemble exams, publish them, and review
          every student's results in one place.
        </p>
      </div>
    </div>
  </div>
  <div class="col-md-4">
    <div class="card h-100 border-0 shadow-sm">
      <div class="card-body">
        <h5 class="card-title">Fair &amp; Timed</h5>
        <p class="card-text text-muted">
          The exam clock is enforced on the server, answers are graded
          automatically, and each exam allows a single attempt.
        </p>
      </div>
    </div>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
