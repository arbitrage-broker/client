package com.arbitragebroker.client.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {
    private HttpStatus httpStatus;
    private String code;

    public BaseException(String message, HttpStatus httpStatus,String code, Throwable e) {
        super(message, e);
        this.httpStatus = httpStatus;
        this.code = code;
    }
    public BaseException(String message, HttpStatus httpStatus, Throwable e) {
        super(message, e);
        this.httpStatus = httpStatus;
    }

    public BaseException(String message, HttpStatus httpStatus, String code) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public BaseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;

    }
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    public ErrorResponse toErrorResponse(String path) {
        return new ErrorResponse(httpStatus, path, getMessage(), code);
    }
}
