package org.wyx.diego.pontifex.annotation;

import java.lang.annotation.*;

/**
 * @author diego
 * @time 2015-07-08
 * @description
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ComponentMetas {

    ComponentMeta[] value();

}
