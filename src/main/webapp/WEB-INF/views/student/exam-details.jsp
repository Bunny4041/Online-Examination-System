<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${exam.examName}" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<nav aria-label="breadcrumb">
  <ol class="breadcrumb">
    <li class="breadcrumb-item"><a href="${ctx}/student/search">Available exams</a></li>
    <li class="breadcrumb-item active" aria-current="page"><c:out value="${exam.examName}"/></li>
  </ol>
</nav>

<c:if test="${notice == 'ineligible'}">
  <div class="alert alert-warning">
    You can't start this exam right now. It may have closed, have no questions yet,
    or you may have already attempted it.
  </div>
</c:if>

<div class="card border-0 shadow-sm">
  <div class="card-body p-4">
    <h1 class="h4 mb-2"><c:out value="${exam.examName}"/></h1>
    <p class="text-muted"><c:out value="${exam.description}"/></p>

    <div class="row g-3 my-2">
      <div class="col-6 col-md-3">
        <div class="border rounded p-3 text-center">
          <div class="h5 mb-0">${exam.durationMin} min</div>
          <div class="text-muted small">Duration</div>
        </div>
      </div>
      <div class="col-6 col-md-3">
        <div class="border rounded p-3 text-center">
          <div class="h5 mb-0">${questionCount}</div>
          <div class="text-muted small">Questions</div>
        </div>
      </div>
      <div class="col-6 col-md-3">
        <div class="border rounded p-3 text-center">
          <div class="h5 mb-0">${allocatedMarks}</div>
          <div class="text-muted small">Total marks</div>
        </div>
      </div>
      <div class="col-6 col-md-3">
        <div class="border rounded p-3 text-center">
          <div class="h5 mb-0">${exam.passingMarks}</div>
          <div class="text-muted small">Pass mark</div>
        </div>
      </div>
    </div>

    <p class="small text-muted">
      Open window: ${exam.startDateFormatted} &ndash; ${exam.endDateFormatted}
    </p>

    <hr>

    <c:choose>
      <c:when test="${attempted}">
        <div class="alert alert-secondary mb-0">
          You have already attempted this exam.
          <a href="${ctx}/student/my-results">View your result</a>.
        </div>
      </c:when>
      <c:when test="${not open}">
        <div class="alert alert-secondary mb-0">
          This exam is not open for attempts at the moment.
        </div>
      </c:when>
      <c:when test="${questionCount == 0}">
        <div class="alert alert-secondary mb-0">
          This exam has no questions yet. Please check back later.
        </div>
      </c:when>
      <c:when test="${eligible}">
        <div class="alert alert-info">
          Once you press <strong>Start exam</strong>, the ${exam.durationMin}-minute
          timer begins immediately and runs on the server &mdash; it keeps counting
          even if you close the page. You may attempt this exam only once.
        </div>
        <form action="${ctx}/student/start-exam" method="post"
              onsubmit="return confirm('Start now? The timer will begin immediately.');">
          <input type="hidden" name="examId" value="${exam.examId}">
          <button type="submit" class="btn btn-success btn-lg">Start exam</button>
          <a class="btn btn-outline-secondary btn-lg" href="${ctx}/student/search">Back</a>
        </form>
      </c:when>
      <c:otherwise>
        <div class="alert alert-secondary mb-0">You can't start this exam right now.</div>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
