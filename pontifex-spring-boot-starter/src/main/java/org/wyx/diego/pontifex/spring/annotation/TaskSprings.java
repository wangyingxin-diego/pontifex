package org.wyx.diego.pontifex.spring.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author wangyingxin
 * @title: TaskSprings
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
public @interface TaskSprings {
    TaskSpring[] value();
}
