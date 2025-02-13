package com.arbitragebroker.client.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends BaseException {
    private static final Logger log = LoggerFactory.getLogger(ConflictException.class);
    public ConflictException(String tableName, String field) {
        super(String.format("%s %s is already in use.",tableName, field), HttpStatus.CONFLICT);
        log.error(
                String.valueOf(this)
        );
    }
    public ConflictException(String msg) {
        super(msg, HttpStatus.CONFLICT);
        log.error(
                String.valueOf(this)
        );
    }
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
