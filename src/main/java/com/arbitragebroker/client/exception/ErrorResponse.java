package com.arbitragebroker.client.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String path;
    private Object message;
    private String code;

    public ErrorResponse(HttpStatus status, String path, Object message, String code) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.path = path;
        this.message = message;
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }
    public ErrorResponse(HttpStatus status, String path, Object message) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.path = path;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }

    public Object getMessage() {
        return message;
    }
}
