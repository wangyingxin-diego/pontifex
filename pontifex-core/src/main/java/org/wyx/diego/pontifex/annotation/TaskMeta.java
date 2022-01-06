package org.wyx.diego.pontifex.annotation;

import org.wyx.diego.pontifex.log.TaskLogLevel;

import java.lang.annotation.*;

/**
 * @author diego
 * @time 2015-07-08
 * @description
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TaskMetas.class)
@Inherited
public @interface TaskMeta {

    String name() default "";

    String pipelineName() default "";

    int sort() default 1;

    TaskLogLevel level() default TaskLogLevel.PAYLOAD;

    RuntimeMeta runtime() default @RuntimeMeta(open = false);

}

