package com.onlineexam.model;

import java.time.LocalDateTime;

/**
 * A user account — either an ADMIN or a STUDENT (distinguished by {@link #role}).
 * Plain JavaBean: one field per {@code users} column, no logic.
 */
public class User {

    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String role;    // "ADMIN" | "STUDENT"
    private String status;  // "ACTIVE" | "INACTIVE"
    private LocalDateTime createdAt;

    public User() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /** True when role is ADMIN (convenience for JSP/servlet checks). */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /** True when the account is ACTIVE and may log in / take exams. */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
}
