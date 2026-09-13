/*
 * exam-timer.js — client-side countdown for the take-exam page.
 *
 * IMPORTANT: this is a display/convenience only. The real deadline is enforced
 * on the SERVER (TakeExamServlet recomputes the remaining time from the stored
 * start_time on every request, and SubmitExamServlet/SubmissionService finalise
 * the attempt). If this script is blocked, paused, or tampered with, the student
 * gains nothing — the server still submits the paper when the time is up.
 *
 * It reads the authoritative seconds-remaining that the servlet rendered into
 * the #exam-timer element's data-remaining attribute, counts down from there,
 * and auto-submits the exam form once when it reaches zero.
 */
(function () {
  "use strict";

  var el = document.getElementById("exam-timer");
  if (!el) {
    return; // not on the exam page
  }

  var remaining = parseInt(el.getAttribute("data-remaining"), 10);
  if (isNaN(remaining)) {
    return;
  }

  var formId = el.getAttribute("data-submit-form");
  var form = formId ? document.getElementById(formId) : null;
  var submitted = false;

  function pad(n) {
    return (n < 10 ? "0" : "") + n;
  }

  function render(seconds) {
    if (seconds < 0) {
      seconds = 0;
    }
    var h = Math.floor(seconds / 3600);
    var m = Math.floor((seconds % 3600) / 60);
    var s = seconds % 60;
    el.textContent = (h > 0 ? pad(h) + ":" : "") + pad(m) + ":" + pad(s);
  }

  function autoSubmit() {
    if (submitted) {
      return;
    }
    submitted = true;
    if (form) {
      // form.submit() deliberately bypasses the manual button's confirm() dialog:
      // when time is up the paper must go in without waiting for a click.
      form.submit();
    }
  }

  render(remaining);

  var timer = setInterval(function () {
    remaining -= 1;
    render(remaining);

    if (remaining <= 60) {
      el.classList.add("timer-warning");
    }
    if (remaining <= 0) {
      clearInterval(timer);
      autoSubmit();
    }
  }, 1000);
})();
