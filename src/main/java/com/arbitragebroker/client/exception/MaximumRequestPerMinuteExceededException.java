package com.arbitragebroker.client.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class MaximumRequestPerMinuteExceededException extends BaseException {
    private static final Logger log = LoggerFactory.getLogger(MaximumRequestPerMinuteExceededException.class);
    public MaximumRequestPerMinuteExceededException() {
        super("you've hit the maximum request limit for now! please wait a moment before trying again.",HttpStatus.TOO_MANY_REQUESTS);
        log.error(
                String.valueOf(this)
        );
    }
    public MaximumRequestPerMinuteExceededException(String msg) {
        super(String.format("%s",msg), HttpStatus.TOO_MANY_REQUESTS);
        log.error(
                String.valueOf(this)
        );
    }
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
