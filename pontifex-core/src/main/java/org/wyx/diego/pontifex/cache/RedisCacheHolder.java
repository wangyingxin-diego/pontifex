package org.wyx.diego.pontifex.cache;

import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RedisCacheHolder extends BaseCacheHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheHolder.class);

    private RedissonClient redissonClient;

    public RedisCacheHolder(CacheHolder parent, RedissonClient redissonClient) {
        super.parent = parent;
        this.redissonClient = redissonClient;
    }

    @Override
    protected void put(String getKey, Object object) {
        try {
            redissonClient.getBucket(getKey).set(object);
        } catch (Exception e) {
            LOGGER.info("pontifex cache redis put exception, cacheKey={}", getKey, e);
        }
    }

    @Override
    protected void put(String getKey, Object object, long timeout) {
        try {
            redissonClient.getBucket(getKey).set(object, timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOGGER.info("pontifex cache redis put exception, cacheKey={}", getKey, e);
        }
    }

    @Override
    protected Target getTarget() {
        return Target.Redis;
    }

    @Override
    public Object get(String key) {
        if(key == null || "".equals(key.trim())) {
            return null;
        }
        try {
            return redissonClient.getBucket(key).get();
        } catch (Exception e) {
            LOGGER.info("pontifex cache redis get exception, cacheKey={}", key, e);
        }
        return null;
    }


    @Override
    protected void delete(String key) {
        try {
            redissonClient.getBucket(key).delete();
        } catch (Exception e) {
            LOGGER.info("pontifex cache redis delete exception, cacheKey={}", key, e);
        }
    }
}
