<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Create exam" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<nav aria-label="breadcrumb">
  <ol class="breadcrumb">
    <li class="breadcrumb-item"><a href="${ctx}/admin/exams">Exams</a></li>
    <li class="breadcrumb-item active" aria-current="page">Create exam</li>
  </ol>
</nav>

<div class="card border-0 shadow-sm">
  <div class="card-body p-4">
    <h1 class="h4 mb-3">Create exam</h1>

    <c:if test="${not empty errors}">
      <div class="alert alert-danger"><ul class="mb-0">
        <c:forEach var="e" items="${errors}"><li><c:out value="${e}"/></li></c:forEach>
      </ul></div>
    </c:if>

    <form action="${ctx}/admin/exams" method="post" novalidate>
      <input type="hidden" name="action" value="create">

      <div class="mb-3">
        <label class="form-label" for="examName">Exam name</label>
        <input type="text" class="form-control" id="examName" name="examName"
               value="<c:out value='${exam.examName}'/>" required>
      </div>

      <div class="mb-3">
        <label class="form-label" for="description">Description</label>
        <textarea class="form-control" id="description" name="description" rows="2"><c:out value="${exam.description}"/></textarea>
      </div>

      <div class="row g-3">
        <div class="col-md-4">
          <label class="form-label" for="durationMin">Duration (minutes)</label>
          <input type="number" min="1" class="form-control" id="durationMin" name="durationMin"
                 value="${exam.durationMin > 0 ? exam.durationMin : ''}" required>
        </div>
        <div class="col-md-4">
          <label class="form-label" for="maxMarks">Maximum marks</label>
          <input type="number" min="1" class="form-control" id="maxMarks" name="maxMarks"
                 value="${exam.maxMarks > 0 ? exam.maxMarks : ''}" required>
        </div>
        <div class="col-md-4">
          <label class="form-label" for="passingMarks">Passing marks</label>
          <input type="number" min="0" class="form-control" id="passingMarks" name="passingMarks"
                 value="${exam.maxMarks > 0 ? exam.passingMarks : ''}" required>
        </div>
      </div>

      <div class="row g-3 mt-0">
        <div class="col-md-6">
          <label class="form-label" for="startDate">Available from</label>
          <input type="datetime-local" class="form-control" id="startDate" name="startDate"
                 value="${exam.startDateForInput}" required>
        </div>
        <div class="col-md-6">
          <label class="form-label" for="endDate">Available until</label>
          <input type="datetime-local" class="form-control" id="endDate" name="endDate"
                 value="${exam.endDateForInput}" required>
        </div>
      </div>

      <div class="mt-4">
        <button type="submit" class="btn btn-primary">Create exam</button>
        <a class="btn btn-outline-secondary" href="${ctx}/admin/exams">Cancel</a>
      </div>
      <p class="text-muted small mt-3 mb-0">
        New exams start as a <strong>Draft</strong>. Allocate questions, then publish
        to make the exam visible to students.
      </p>
    </form>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
