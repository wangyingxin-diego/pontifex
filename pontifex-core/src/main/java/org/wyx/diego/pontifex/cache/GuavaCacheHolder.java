package org.wyx.diego.pontifex.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author wangyingxin
 * @title: GuavaCacheHolder
 * @projectName pontifex
 * @description: TODO
 * @date 2015/11/11
 */
public class GuavaCacheHolder extends BaseCacheHolder {
    private LoadingCache<String, Object> cache;

    public GuavaCacheHolder(CacheBean cacheBean) {
        this.cache = CacheBuilder.newBuilder().maximumSize(cacheBean.getMaximumSize()).expireAfterWrite(cacheBean.getTimeout(), TimeUnit.MILLISECONDS).build(new CacheLoader<String, Object>() {
            public Object load(String key) throws IOException {
                return GuavaCacheHolder.this.getParent().get(key);
            }
        });
    }

    public Object get(String key) {
        try {
            return this.cache.get(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
