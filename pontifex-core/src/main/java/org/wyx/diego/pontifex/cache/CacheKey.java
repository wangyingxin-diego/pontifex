package org.wyx.diego.pontifex.cache;

public class CacheKey {

    private GetKey getKey;

    private CacheBean cacheBean;

    public CacheKey(GetKey getKey, CacheBean cacheBean) {
        this.getKey = getKey;
        this.cacheBean = cacheBean;
    }

    public GetKey getGetKey() {
        return getKey;
    }

    public CacheBean getCacheBean() {
        return cacheBean;
    }

}
