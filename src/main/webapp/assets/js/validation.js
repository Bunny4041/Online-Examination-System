/*
 * validation.js — light client-side form validation (progressive enhancement).
 *
 * This is ONLY a convenience so users get instant feedback. Every rule enforced
 * here is ALSO enforced on the server in the Servlets, which is authoritative —
 * the app is fully usable with JavaScript disabled.
 *
 * Behaviour: for each POST form that has required fields (except the exam paper),
 * block submission while any field is invalid and show Bootstrap's validation
 * styling. Nothing custom — it just leans on the browser's built-in checks.
 */
(function () {
  "use strict";

  var forms = document.querySelectorAll('form[method="post"]');

  Array.prototype.forEach.call(forms, function (form) {
    // The exam paper has no required inputs (a blank answer is allowed) and its
    // own auto-submit logic, so leave it alone.
    if (form.id === "exam-form") {
      return;
    }
    // Nothing to validate if there are no required fields.
    if (!form.querySelector("[required]")) {
      return;
    }

    // Suppress the browser's default bubbles; we show Bootstrap styling instead.
    form.noValidate = true;

    form.addEventListener(
      "submit",
      function (event) {
        if (!form.checkValidity()) {
          event.preventDefault();
          event.stopPropagation();
        }
        form.classList.add("was-validated");
      },
      false
    );
  });
})();
