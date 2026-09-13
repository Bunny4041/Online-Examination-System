<%--
  Common page header: opens the HTML document, loads Bootstrap + our stylesheet,
  and renders a role-aware navigation bar. Included at the TOP of every page with:
      <c:set var="pageTitle" value="..."/>
      <%@ include file="/WEB-INF/views/common/header.jsp" %>
  The including page owns the <%@ page %> directive (content type); this fragment
  only declares the taglibs it uses so there is no duplicate page directive.
--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${empty pageTitle ? 'Online Examination System' : pageTitle}"/> &middot; Online Exam</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${ctx}/assets/css/style.css" rel="stylesheet">
</head>
<body class="bg-light d-flex flex-column min-vh-100">
<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
  <div class="container">
    <a class="navbar-brand fw-bold" href="${ctx}/">Online Examination System</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
            data-bs-target="#mainNav" aria-controls="mainNav"
            aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="mainNav">
      <ul class="navbar-nav ms-auto align-items-lg-center">
        <c:choose>
          <c:when test="${sessionScope.role == 'ADMIN'}">
            <li class="nav-item"><a class="nav-link" href="${ctx}/admin/dashboard">Dashboard</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/admin/students">Students</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/admin/exams">Exams</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/admin/questions">Questions</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/admin/allocate">Allocate</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/admin/results">Results</a></li>
            <li class="nav-item ms-lg-3"><span class="navbar-text small">
              <c:out value="${sessionScope.username}"/> (Admin)</span></li>
            <li class="nav-item"><a class="nav-link fw-semibold" href="${ctx}/logout">Logout</a></li>
          </c:when>
          <c:when test="${sessionScope.role == 'STUDENT'}">
            <li class="nav-item"><a class="nav-link" href="${ctx}/student/dashboard">Dashboard</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/student/search">Exams</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/student/my-results">My Results</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/student/history">History</a></li>
            <li class="nav-item ms-lg-3"><span class="navbar-text small">
              <c:out value="${sessionScope.username}"/></span></li>
            <li class="nav-item"><a class="nav-link fw-semibold" href="${ctx}/logout">Logout</a></li>
          </c:when>
          <c:otherwise>
            <li class="nav-item"><a class="nav-link" href="${ctx}/login">Login</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/register">Register</a></li>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>
  </div>
</nav>
<main class="container py-4 flex-grow-1">
