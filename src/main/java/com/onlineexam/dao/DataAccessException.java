package com.onlineexam.dao;

/**
 * Unchecked exception that wraps low-level {@link java.sql.SQLException}s thrown
 * inside the DAO layer. Servlets catch this (or let it bubble to the 500 error
 * page) instead of dealing with SQL plumbing directly.
 */
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
