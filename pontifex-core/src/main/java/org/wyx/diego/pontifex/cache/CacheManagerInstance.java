package org.wyx.diego.pontifex.cache;

public enum CacheManagerInstance {

    INSTANCE(new CacheManager());


    private CacheManager cacheManager;

    CacheManagerInstance(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }
}
