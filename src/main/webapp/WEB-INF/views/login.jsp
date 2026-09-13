<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row justify-content-center">
  <div class="col-md-6 col-lg-5">
    <div class="card border-0 shadow-sm">
      <div class="card-body p-4">
        <h1 class="h4 mb-3 text-center">Sign in</h1>

        <%-- Success flash after registering (?registered=1) --%>
        <c:if test="${param.registered == '1'}">
          <div class="alert alert-success py-2" role="alert">
            Account created. Please sign in.
          </div>
        </c:if>

        <%-- Generic auth error set by LoginServlet --%>
        <c:if test="${not empty error}">
          <div class="alert alert-danger py-2" role="alert">
            <c:out value="${error}"/>
          </div>
        </c:if>

        <form action="${ctx}/login" method="post" novalidate>
          <div class="mb-3">
            <label for="username" class="form-label">Username</label>
            <input type="text" class="form-control" id="username" name="username"
                   value="<c:out value='${username}'/>" required autofocus>
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">Password</label>
            <input type="password" class="form-control" id="password" name="password" required>
          </div>
          <button type="submit" class="btn btn-primary w-100">Sign in</button>
        </form>

        <p class="text-center text-muted mt-3 mb-0">
          New here? <a href="${ctx}/register">Create an account</a>
        </p>
      </div>
    </div>
    <p class="text-center text-muted small mt-3">
      Administrators sign in with the seeded admin account (see the setup guide).
    </p>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
