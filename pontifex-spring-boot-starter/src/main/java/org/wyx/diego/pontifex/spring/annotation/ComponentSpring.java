package org.wyx.diego.pontifex.spring.annotation;

import org.springframework.stereotype.Component;
import org.wyx.diego.pontifex.annotation.ComponentMeta;
import org.wyx.diego.pontifex.annotation.ComponentMetas;

import java.lang.annotation.*;

/**
 * @author wangyingxin
 * @title: ComponentSpring
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/4
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(ComponentSprings.class)
@Component
public @interface ComponentSpring {

    ComponentMeta componentMeta();

}
