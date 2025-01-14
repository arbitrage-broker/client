package com.eshop.client.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class InsufficentBalanceException extends BaseException {
    private static final Logger log = LoggerFactory.getLogger(InsufficentBalanceException.class);
    public InsufficentBalanceException() {
        super("Insufficient balance!", HttpStatus.PAYMENT_REQUIRED);
        log.error(
                String.valueOf(this)
        );
    }
    public InsufficentBalanceException(String msg) {
        super(String.format("%s",msg), HttpStatus.PAYMENT_REQUIRED);
        log.error(
                String.valueOf(this)
        );
    }
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
