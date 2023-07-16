package org.wyx.diego.pontifex.demo;

import org.wyx.diego.pontifex.cache.CacheConfig;
import org.wyx.diego.pontifex.flow.config.FlowConfig;
import org.wyx.diego.pontifex.flow.sentinel.SentinelFlowDeGradePipelineInterface;

public class MyConfig implements CacheConfig {

    @Override
    public boolean open() {
        return true;
    }

    @Override
    public long maxMemorySize() {
        return 100000;
    }

    @Override
    public long timeout() {
        return 1000;
    }

}
