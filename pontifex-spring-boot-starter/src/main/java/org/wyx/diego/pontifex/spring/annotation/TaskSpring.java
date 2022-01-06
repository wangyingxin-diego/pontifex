package org.wyx.diego.pontifex.spring.annotation;

import org.springframework.stereotype.Component;
import org.wyx.diego.pontifex.annotation.TaskMeta;

/**
 * @author wangyingxin
 * @title: TaskSpring
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/4
 */
@Component
@TaskMeta
public @interface TaskSpring {

    TaskMeta taskMeta();

}
