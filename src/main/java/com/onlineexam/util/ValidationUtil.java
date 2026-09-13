package com.onlineexam.util;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Server-side validation that mirrors the client-side rules in
 * {@code js/validation.js}. Every servlet re-validates through this class before
 * touching the database, because client checks can be bypassed.
 */
public final class ValidationUtil {

    // 3-50 chars: letters, digits, underscore
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,50}$");
    // pragmatic email check: something@something.something, no spaces
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private ValidationUtil() {
        // utility class — no instances
    }

    /** @return true if the string is null or only whitespace. */
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME.matcher(username).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.length() <= 120 && EMAIL.matcher(email).matches();
    }

    /** At least 6 characters, containing at least one letter and one digit. */
    public static boolean isStrongPassword(String pw) {
        if (pw == null || pw.length() < 6) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (int i = 0; i < pw.length(); i++) {
            char c = pw.charAt(i);
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        return hasLetter && hasDigit;
    }

    public static boolean isPositiveInt(int value) {
        return value > 0;
    }

    /** Exam window is valid when both dates exist and end is not before start. */
    public static boolean datesValid(LocalDateTime start, LocalDateTime end) {
        return start != null && end != null && !end.isBefore(start);
    }

    /** Marks are valid when max &gt; 0 and 0 &lt;= passing &lt;= max. */
    public static boolean marksValid(int passingMarks, int maxMarks) {
        return maxMarks > 0 && passingMarks >= 0 && passingMarks <= maxMarks;
    }

    /**
     * Safe integer parse for request parameters.
     *
     * @return the parsed int, or {@code defaultValue} if the text is not an int.
     */
    public static int parseIntOrDefault(String text, int defaultValue) {
        if (text == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
