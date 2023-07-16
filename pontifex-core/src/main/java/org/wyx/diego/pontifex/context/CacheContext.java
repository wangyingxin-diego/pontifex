package org.wyx.diego.pontifex.context;

import org.wyx.diego.pontifex.cache.CacheManager;

public class CacheContext {

    private CacheManager cacheManager;

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public CacheContext setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        return this;
    }
}
