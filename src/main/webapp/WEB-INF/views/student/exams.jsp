<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Available exams" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 class="h3 mb-3">Available exams</h1>

<form class="row g-2 mb-4" action="${ctx}/student/search" method="get">
  <div class="col-sm-8 col-md-6">
    <input type="text" class="form-control" name="q" placeholder="Search by exam name"
           value="<c:out value='${q}'/>">
  </div>
  <div class="col-auto">
    <button class="btn btn-primary" type="submit">Search</button>
    <a class="btn btn-outline-secondary" href="${ctx}/student/search">Clear</a>
  </div>
</form>

<c:choose>
  <c:when test="${empty exams}">
    <div class="alert alert-info">
      No exams are open for you right now. Please check back later.
    </div>
  </c:when>
  <c:otherwise>
    <div class="row g-3">
      <c:forEach var="exam" items="${exams}">
        <div class="col-md-6 col-lg-4">
          <div class="card border-0 shadow-sm h-100">
            <div class="card-body d-flex flex-column">
              <h2 class="h5"><c:out value="${exam.examName}"/></h2>
              <p class="text-muted small flex-grow-1">
                <c:out value="${exam.description}"/>
              </p>
              <ul class="list-unstyled small mb-3">
                <li><strong>Duration:</strong> ${exam.durationMin} min</li>
                <li><strong>Questions:</strong> ${exam.questionCount}</li>
                <li><strong>Total marks:</strong> ${exam.maxMarks}</li>
                <li><strong>Open until:</strong> ${exam.endDateFormatted}</li>
              </ul>
              <c:choose>
                <c:when test="${attemptedExamIds.contains(exam.examId)}">
                  <button class="btn btn-outline-secondary" disabled>Already attempted</button>
                </c:when>
                <c:otherwise>
                  <a class="btn btn-primary"
                     href="${ctx}/student/exam-details?examId=${exam.examId}">View &amp; start</a>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
