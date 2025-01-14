package com.arbitragebroker.client.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class ExpectationException extends BaseException {
    private static final Logger log = LoggerFactory.getLogger(ExpectationException.class);
    public ExpectationException() {
        super("not allowed!",HttpStatus.EXPECTATION_FAILED);
        log.error(
                String.valueOf(this)
        );
    }
    public ExpectationException(String msg) {
        super(String.format("%s",msg), HttpStatus.EXPECTATION_FAILED);
        log.error(
                String.valueOf(this)
        );
    }
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
