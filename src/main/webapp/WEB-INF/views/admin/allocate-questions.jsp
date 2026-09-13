<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Allocate questions" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 class="h3 mb-3">Allocate questions to an exam</h1>

<form class="row g-2 mb-4" action="${ctx}/admin/allocate" method="get">
  <div class="col-sm-8 col-md-6">
    <select class="form-select" name="examId" onchange="this.form.submit()">
      <option value="">Choose an exam…</option>
      <c:forEach var="ex" items="${exams}">
        <option value="${ex.examId}" ${not empty exam and exam.examId == ex.examId ? 'selected' : ''}>
          #${ex.examId} — <c:out value="${ex.examName}"/> (${ex.status})
        </option>
      </c:forEach>
    </select>
  </div>
  <div class="col-auto">
    <button class="btn btn-primary" type="submit">Load</button>
  </div>
</form>

<c:choose>
  <c:when test="${empty exam}">
    <div class="alert alert-info">Select an exam above to manage its questions.</div>
  </c:when>
  <c:otherwise>
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
      <div>
        <h2 class="h5 mb-0"><c:out value="${exam.examName}"/></h2>
        <span class="text-muted small">
          Allocated marks: <strong>${allocatedMarks}</strong> &middot;
          Exam max marks: <strong>${exam.maxMarks}</strong>
        </span>
      </div>
      <a class="btn btn-sm btn-outline-secondary" href="${ctx}/admin/exams">Back to exams</a>
    </div>

    <c:if test="${allocatedMarks ne exam.maxMarks}">
      <div class="alert alert-warning py-2 small">
        Heads up: the allocated marks (${allocatedMarks}) don't match the exam's
        maximum marks (${exam.maxMarks}). Results are graded out of the allocated
        total, so make sure this is intentional.
      </div>
    </c:if>

    <div class="row g-4">
      <div class="col-lg-6">
        <div class="card border-0 shadow-sm h-100">
          <div class="card-header bg-white fw-semibold">
            Allocated (${fn:length(allocatedQuestions)})
          </div>
          <ul class="list-group list-group-flush">
            <c:choose>
              <c:when test="${empty allocatedQuestions}">
                <li class="list-group-item text-muted">No questions allocated yet.</li>
              </c:when>
              <c:otherwise>
                <c:forEach var="qn" items="${allocatedQuestions}">
                  <li class="list-group-item d-flex justify-content-between align-items-start gap-2">
                    <div>
                      <div><c:out value="${qn.questionText}"/></div>
                      <span class="badge text-bg-light border">Correct: ${qn.correctOption}</span>
                      <span class="badge text-bg-light border">${qn.marks} marks</span>
                    </div>
                    <form action="${ctx}/admin/allocate" method="post">
                      <input type="hidden" name="action" value="deallocate">
                      <input type="hidden" name="examId" value="${exam.examId}">
                      <input type="hidden" name="questionId" value="${qn.questionId}">
                      <button class="btn btn-sm btn-outline-danger">Remove</button>
                    </form>
                  </li>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </ul>
        </div>
      </div>

      <div class="col-lg-6">
        <div class="card border-0 shadow-sm h-100">
          <div class="card-header bg-white fw-semibold">
            Available (${fn:length(availableQuestions)})
          </div>
          <ul class="list-group list-group-flush">
            <c:choose>
              <c:when test="${empty availableQuestions}">
                <li class="list-group-item text-muted">
                  No more active questions to add. Create more in the question bank.
                </li>
              </c:when>
              <c:otherwise>
                <c:forEach var="qn" items="${availableQuestions}">
                  <li class="list-group-item d-flex justify-content-between align-items-start gap-2">
                    <div>
                      <div><c:out value="${qn.questionText}"/></div>
                      <span class="badge text-bg-light border">${qn.marks} marks</span>
                    </div>
                    <form action="${ctx}/admin/allocate" method="post">
                      <input type="hidden" name="action" value="allocate">
                      <input type="hidden" name="examId" value="${exam.examId}">
                      <input type="hidden" name="questionId" value="${qn.questionId}">
                      <button class="btn btn-sm btn-outline-success">Add</button>
                    </form>
                  </li>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </ul>
        </div>
      </div>
    </div>
  </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
