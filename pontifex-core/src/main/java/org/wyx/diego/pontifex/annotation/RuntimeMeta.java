package org.wyx.diego.pontifex.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

/**
 * @author diego
 * @time 2015-10-12
 * @description
 */
@Target({ElementType.TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RuntimeMeta {

    boolean open() default true;

    long timeout() default 50L;

    int logLevel() default 50;

    Alarm[] alarm() default {Alarm.LOG};

}
