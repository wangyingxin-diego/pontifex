package org.wyx.diego.pontifex.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author wangyingxin
 * @title: GuavaCacheHolder
 * @projectName pontifex
 * @description: TODO
 * @date 2015/11/11
 */
public class GuavaCacheHolder extends BaseCacheHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuavaCacheHolder.class);
    private final LoadingCache<String, Object> cache;

    public GuavaCacheHolder(CacheConfig cacheConfig, CacheHolder parent) {
        super.parent = parent;
        this.cache = CacheBuilder.newBuilder().maximumSize(cacheConfig.maxMemorySize()).expireAfterWrite(cacheConfig.timeout(), TimeUnit.MILLISECONDS).build(new CacheLoader<String, Object>() {
            public Object load(String key) throws IOException {
                return exceptionResult;
            }
        });
    }

    @Override
    protected Object get(String key) {

        Object object = null;
        try {
            object = cache.get(key);
        } catch (Throwable e) {
            LOGGER.info("pontifex cache get exception, cacheKey={}", key, e);
        }

        return object;
    }

    @Override
    protected void put(String key, Object object) {

        try {
            cache.put(key, object);
        } catch (Exception e) {
            LOGGER.info("pontifex cache set exception, cacheKey={}", key, e);
        }

    }

    @Override
    protected void put(String key, Object object, long timeout) {
        try {
            cache.put(key, object);
        } catch (Exception e) {
            LOGGER.info("pontifex cache set exception, cacheKey={}", key, e);
        }

    }

    @Override
    protected void delete(String key) {
        try {
            cache.invalidate(key);
        } catch (Exception e) {
            LOGGER.info("pontifex cache delete exception, cacheKey={}", key, e);
        }
    }

    @Override
    protected Target getTarget() {
        return Target.Memory;
    }
}
