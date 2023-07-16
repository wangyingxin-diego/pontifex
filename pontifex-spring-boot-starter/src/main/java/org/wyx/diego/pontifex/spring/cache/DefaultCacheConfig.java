package org.wyx.diego.pontifex.spring.cache;

import org.wyx.diego.pontifex.cache.AbstractCacheConfig;

public class DefaultCacheConfig extends AbstractCacheConfig {


    @Override
    public boolean open() {
        return false;
    }

    @Override
    public long maxMemorySize() {
        return 128000000;
    }

    @Override
    public long timeout() {
        return 100;
    }
}
