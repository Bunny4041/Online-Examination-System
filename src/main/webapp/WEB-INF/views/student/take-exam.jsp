<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Taking: ${exam.examName}" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
  <div>
    <h1 class="h4 mb-0"><c:out value="${exam.examName}"/></h1>
    <span class="text-muted small">${exam.durationMin}-minute exam &middot; answer all questions</span>
  </div>
  <div class="text-end">
    <div class="text-muted small">Time remaining</div>
    <div id="exam-timer" class="fs-3 fw-bold text-primary"
         data-remaining="${remainingSeconds}"
         data-submit-form="exam-form">--:--</div>
  </div>
</div>

<noscript>
  <div class="alert alert-warning">
    JavaScript is off, so the on-screen countdown won't move. Don't worry &mdash; the
    real deadline is enforced on the server, so submit before your time is up.
  </div>
</noscript>

<div class="alert alert-info py-2 small">
  The timer runs on the server. If you close or reload this page it keeps counting,
  and when it reaches zero your exam is submitted automatically.
</div>

<form id="exam-form" action="${ctx}/student/submit-exam" method="post">
  <input type="hidden" name="attemptId" value="${attempt.attemptId}">

  <c:forEach var="q" items="${questions}" varStatus="loop">
    <div class="card border-0 shadow-sm mb-3">
      <div class="card-body">
        <div class="d-flex justify-content-between">
          <h2 class="h6">Question ${loop.index + 1}</h2>
          <span class="badge text-bg-light border align-self-start">${q.marks} marks</span>
        </div>
        <p class="mb-3"><c:out value="${q.questionText}"/></p>

        <div class="form-check">
          <input class="form-check-input" type="radio" name="q_${q.questionId}"
                 id="q_${q.questionId}_A" value="A">
          <label class="form-check-label" for="q_${q.questionId}_A">
            <c:out value="${q.optionA}"/>
          </label>
        </div>
        <div class="form-check">
          <input class="form-check-input" type="radio" name="q_${q.questionId}"
                 id="q_${q.questionId}_B" value="B">
          <label class="form-check-label" for="q_${q.questionId}_B">
            <c:out value="${q.optionB}"/>
          </label>
        </div>
        <div class="form-check">
          <input class="form-check-input" type="radio" name="q_${q.questionId}"
                 id="q_${q.questionId}_C" value="C">
          <label class="form-check-label" for="q_${q.questionId}_C">
            <c:out value="${q.optionC}"/>
          </label>
        </div>
        <div class="form-check">
          <input class="form-check-input" type="radio" name="q_${q.questionId}"
                 id="q_${q.questionId}_D" value="D">
          <label class="form-check-label" for="q_${q.questionId}_D">
            <c:out value="${q.optionD}"/>
          </label>
        </div>
      </div>
    </div>
  </c:forEach>

  <div class="d-flex justify-content-end gap-2 pb-4">
    <button type="submit" class="btn btn-success btn-lg"
            onclick="return confirm('Submit your exam? You cannot change your answers afterwards.');">
      Submit exam
    </button>
  </div>
</form>

<script src="${ctx}/assets/js/exam-timer.js"></script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
