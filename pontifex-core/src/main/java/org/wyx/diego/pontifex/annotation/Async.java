package org.wyx.diego.pontifex.annotation;

/**
 * @author wangyingxin
 * @title: Async
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/31
 */
public @interface Async {

    boolean isOpen() default true;

    short concurrency() default 3;

}
