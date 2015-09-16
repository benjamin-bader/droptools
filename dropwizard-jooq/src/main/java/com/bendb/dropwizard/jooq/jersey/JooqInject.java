package com.bendb.dropwizard.jooq.jersey;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JooqInject {
    String value() default "";
}
