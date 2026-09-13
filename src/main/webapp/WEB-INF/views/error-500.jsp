<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Something went wrong" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="text-center py-5">
  <p class="display-1 fw-bold text-danger mb-0">500</p>
  <h1 class="h4 mb-3">Something went wrong</h1>
  <p class="text-muted col-lg-6 mx-auto">
    An unexpected error occurred while processing your request. The details have
    been logged on the server. Please try again in a moment.
  </p>
  <div class="mt-4">
    <a class="btn btn-primary" href="${ctx}/">Go to home</a>
  </div>
</div>

<%-- Note: we deliberately do NOT print the exception/stack trace to the user. --%>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
