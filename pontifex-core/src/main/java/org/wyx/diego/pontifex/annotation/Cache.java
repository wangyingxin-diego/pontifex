package org.wyx.diego.pontifex.annotation;



import java.lang.annotation.*;

import static org.wyx.diego.pontifex.cache.Target.Memory;

/**
 * @author wangyingxin
 * @title: Cache
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/21
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Cache {
    boolean isOpen() default true;

    org.wyx.diego.pontifex.cache.Target[] target() default {Memory};

    long timeout() default 300000L;

    long maximumSize() default -1L;
}
