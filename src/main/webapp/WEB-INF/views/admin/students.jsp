<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Students" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h3 mb-0">Students</h1>
</div>

<form class="row g-2 mb-3" action="${ctx}/admin/students" method="get">
  <div class="col-sm-8 col-md-6">
    <input type="text" class="form-control" name="q" placeholder="Search by name, username or email"
           value="<c:out value='${q}'/>">
  </div>
  <div class="col-auto">
    <button class="btn btn-primary" type="submit">Search</button>
    <a class="btn btn-outline-secondary" href="${ctx}/admin/students">Clear</a>
  </div>
</form>

<div class="card border-0 shadow-sm">
  <div class="table-responsive">
    <table class="table table-hover align-middle mb-0">
      <thead class="table-light">
        <tr>
          <th>#</th><th>Full name</th><th>Username</th><th>Email</th>
          <th>Status</th><th class="text-end">Actions</th>
        </tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${empty students}">
            <tr><td colspan="6" class="text-center text-muted py-4">No students found.</td></tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="s" items="${students}">
              <tr>
                <td>${s.userId}</td>
                <td><c:out value="${s.fullName}"/></td>
                <td><c:out value="${s.username}"/></td>
                <td><c:out value="${s.email}"/></td>
                <td>
                  <c:choose>
                    <c:when test="${s.active}">
                      <span class="badge text-bg-success">Active</span>
                    </c:when>
                    <c:otherwise>
                      <span class="badge text-bg-secondary">Inactive</span>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td class="text-end">
                  <a class="btn btn-sm btn-outline-primary"
                     href="${ctx}/admin/students?action=view&id=${s.userId}">View</a>
                  <form action="${ctx}/admin/students" method="post" class="d-inline">
                    <input type="hidden" name="id" value="${s.userId}">
                    <c:choose>
                      <c:when test="${s.active}">
                        <input type="hidden" name="action" value="deactivate">
                        <button class="btn btn-sm btn-outline-danger"
                                onclick="return confirm('Deactivate this student?');">Deactivate</button>
                      </c:when>
                      <c:otherwise>
                        <input type="hidden" name="action" value="activate">
                        <button class="btn btn-sm btn-outline-success">Activate</button>
                      </c:otherwise>
                    </c:choose>
                  </form>
                </td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
