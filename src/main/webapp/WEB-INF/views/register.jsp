<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Register" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row justify-content-center">
  <div class="col-md-7 col-lg-6">
    <div class="card border-0 shadow-sm">
      <div class="card-body p-4">
        <h1 class="h4 mb-3 text-center">Create a student account</h1>

        <%-- Validation errors set by RegisterServlet (List<String>) --%>
        <c:if test="${not empty errors}">
          <div class="alert alert-danger" role="alert">
            <ul class="mb-0">
              <c:forEach var="e" items="${errors}">
                <li><c:out value="${e}"/></li>
              </c:forEach>
            </ul>
          </div>
        </c:if>

        <form action="${ctx}/register" method="post" novalidate>
          <div class="mb-3">
            <label for="fullName" class="form-label">Full name</label>
            <input type="text" class="form-control" id="fullName" name="fullName"
                   value="<c:out value='${fullName}'/>" required autofocus>
          </div>
          <div class="mb-3">
            <label for="username" class="form-label">Username</label>
            <input type="text" class="form-control" id="username" name="username"
                   value="<c:out value='${username}'/>" required>
            <div class="form-text">3-50 characters: letters, digits or underscore.</div>
          </div>
          <div class="mb-3">
            <label for="email" class="form-label">Email</label>
            <input type="email" class="form-control" id="email" name="email"
                   value="<c:out value='${email}'/>" required>
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">Password</label>
            <input type="password" class="form-control" id="password" name="password" required>
            <div class="form-text">At least 6 characters, including a letter and a digit.</div>
          </div>
          <button type="submit" class="btn btn-primary w-100">Create account</button>
        </form>

        <p class="text-center text-muted mt-3 mb-0">
          Already registered? <a href="${ctx}/login">Sign in</a>
        </p>
      </div>
    </div>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
