package org.wyx.diego.pontifex.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wyx.diego.pontifex.spring.loader.SpringComponentLoader;
import org.wyx.diego.pontifex.spring.loader.SpringSequencePipelineLoader;
import org.wyx.diego.pontifex.spring.manager.SpringPontifexManager;

/**
 * @author wangyingxin
 * @title: SpringAutoConfiguration
 * @projectName pontifex
 * @description: TODO
 * @date 2021/12/29
 */
@Configuration
public class SpringAutoConfiguration {

    @Bean
    public SpringComponentLoader getSpringComponentLoader() {
        return new SpringComponentLoader();
    }

    @Bean
    public SpringSequencePipelineLoader getSpringSequencePipelineLoader() {
        return new SpringSequencePipelineLoader();
    }

    @Bean
    public SpringPontifexManager getSpringPontifexManager() {
        return new SpringPontifexManager();
    }

}
