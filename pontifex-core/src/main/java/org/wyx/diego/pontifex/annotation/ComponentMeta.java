package org.wyx.diego.pontifex.annotation;

import org.wyx.diego.pontifex.log.ComponentLogLevel;

import java.lang.annotation.*;

/**
 * @author diego
 * @time 2015-07-08
 * @description
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(ComponentMetas.class)
public @interface ComponentMeta {
    String name() default "";

    ComponentLogLevel level() default ComponentLogLevel.SIMPLE;

    Async async() default @Async(
            isOpen = false
    );

    RuntimeMeta runtime() default @RuntimeMeta(
            open = false
    );
}
