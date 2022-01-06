package org.wyx.diego.pontifex.spring.loader;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.wyx.diego.pontifex.loader.ComponentLoaderInstance;

/**
 * @author wangyingxin
 * @title: SpringComponentLoader
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/29
 */
@Component
public class SpringComponentLoader implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if(!(bean instanceof org.wyx.diego.pontifex.Component)) return bean;
        return ComponentLoaderInstance.INSTANCE.load((org.wyx.diego.pontifex.Component) bean);

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
