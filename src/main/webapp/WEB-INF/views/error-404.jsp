<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Page not found" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="text-center py-5">
  <p class="display-1 fw-bold text-primary mb-0">404</p>
  <h1 class="h4 mb-3">We couldn't find that page</h1>
  <p class="text-muted col-lg-6 mx-auto">
    The page or record you asked for doesn't exist (or may have been removed).
    Check the address and try again.
  </p>
  <div class="mt-4">
    <a class="btn btn-primary" href="${ctx}/">Go to home</a>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
