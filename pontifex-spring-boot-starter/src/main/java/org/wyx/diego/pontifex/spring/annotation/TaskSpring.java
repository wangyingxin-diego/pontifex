package org.wyx.diego.pontifex.spring.annotation;

import org.springframework.stereotype.Component;
import org.wyx.diego.pontifex.annotation.TaskMeta;

import java.lang.annotation.*;

/**
 * @author wangyingxin
 * @title: TaskSpring
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(TaskSprings.class)
@Component
public @interface TaskSpring {

    TaskMeta taskMeta() default @TaskMeta();

}
