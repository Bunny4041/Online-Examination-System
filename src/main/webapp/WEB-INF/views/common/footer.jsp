<%--
  Common page footer: closes the <main> opened in header.jsp, adds a small footer
  band, and loads Bootstrap's JS bundle. Included at the BOTTOM of every page:
      <%@ include file="/WEB-INF/views/common/footer.jsp" %>
--%>
</main>
<footer class="border-top py-3 mt-4 bg-white">
  <div class="container text-center text-muted small">
    Online Examination System &middot;
  </div>
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/assets/js/validation.js"></script>
</body>
</html>
