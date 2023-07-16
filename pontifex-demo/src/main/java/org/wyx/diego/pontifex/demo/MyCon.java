package org.wyx.diego.pontifex.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wyx.diego.pontifex.cache.CacheConfig;
import org.wyx.diego.pontifex.flow.config.FlowConfig;

@Configuration
public class MyCon {

    @Bean
    public CacheConfig getCacheConfig() {
        return new MyConfig();
    }

    @Bean
    public FlowConfig getFlowConfig() {
        return new MyFlowConfig();
    }

}


