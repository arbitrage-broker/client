package com.eshop.client.config;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Limited {
    public int requestsPerMinutes() default -1;
}
