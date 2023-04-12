package org.wyx.diego.pontifex.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PipelineMeta {

    String name() default "";

    Cache cache() default @Cache(
            isOpen = false
    );

}
