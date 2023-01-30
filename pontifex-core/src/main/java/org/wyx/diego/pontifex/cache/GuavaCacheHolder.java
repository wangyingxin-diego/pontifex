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
    private final LoadingCache<String, Object> cache;

    public GuavaCacheHolder(CacheBean cacheBean, CacheHolder parent) {
        super.parent = parent;
        this.cache = CacheBuilder.newBuilder().maximumSize(cacheBean.getMaximumSize()).expireAfterWrite(cacheBean.getTimeout(), TimeUnit.MILLISECONDS).build(new CacheLoader<String, Object>() {
            public Object load(String key) throws IOException {
                return GuavaCacheHolder.this.getParent().get(key);
            }
        });
    }

    public Object get(String key) {
        if(key == null || "".equals(key.trim())) {
            return null;
        }
        try {
            if(parent != null) {
                return parent.get(key);
            }
            return this.cache.get(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void put(String key, Object object) {

        try {
            cache.put(key, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            super.put(key, object);
        }

    }
}
