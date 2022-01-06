package org.wyx.diego.pontifex.spring.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.wyx.diego.pontifex.loader.UnifyPipelineLoaderInstance;
import org.wyx.diego.pontifex.pipeline.Task;

@Component
public class SpringSequencePipelineLoader implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SpringSequencePipelineLoader.class);

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if(!(bean instanceof Task)) return bean;

        Task task = (Task) bean;
        UnifyPipelineLoaderInstance.INSTANCE.load(task);
        return bean;

    }

    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }

}
