<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Access denied" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="text-center py-5">
  <p class="display-1 fw-bold text-warning mb-0">403</p>
  <h1 class="h4 mb-3">Access denied</h1>
  <p class="text-muted col-lg-6 mx-auto">
    You are signed in, but this area is restricted to a different role. Students
    cannot open administrator pages, and vice versa.
  </p>
  <div class="mt-4">
    <c:choose>
      <c:when test="${sessionScope.role == 'ADMIN'}">
        <a class="btn btn-primary" href="${ctx}/admin/dashboard">Back to Admin Dashboard</a>
      </c:when>
      <c:when test="${sessionScope.role == 'STUDENT'}">
        <a class="btn btn-primary" href="${ctx}/student/dashboard">Back to My Dashboard</a>
      </c:when>
      <c:otherwise>
        <a class="btn btn-primary" href="${ctx}/login">Sign in</a>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
