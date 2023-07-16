package org.wyx.diego.pontifex.cache;

import org.redisson.api.RedissonClient;
import org.wyx.diego.pontifex.annotation.PipelineMeta;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.exception.ExceptionCode;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangyingxin
 * @title: CacheManager
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/21
 */
public class CacheManager {


    private CacheConfig cacheConfig;
    private RedissonClient redissonClient;
    private CacheHolder cacheHolder;
    private static final ConcurrentHashMap<String, CacheKey> cacheKeyMap = new ConcurrentHashMap();


    public CacheManager() {

    }

    public static void addPipeline(PipelineMeta pipelineMeta, GetKey<?> getKey) {
        String name = pipelineMeta.name();
        if(name == null || "".equals(name.trim())) {

        }
        CacheBean cacheBean = new CacheBean(pipelineMeta.cache());
        getKey = getKey==null? DefaultPipelineGetKey.DEFAULT_GET_KEY:getKey;
        CacheKey cacheKey = new CacheKey(getKey, cacheBean);
        CacheKey has = cacheKeyMap.putIfAbsent(name, cacheKey);
        if(has != null) {
            throw PontifexRuntimeException.exception(ExceptionCode.EXCEPTION_CODE_LOAD_CACHE_KEY_REPETITION);
        }
    }

    public static void addCacheKey(String name, CacheKey cacheKey) {

        CacheKey cacheKey2 = cacheKeyMap.putIfAbsent(name, cacheKey);
        if(cacheKey2 != null) {
            throw PontifexRuntimeException.exception(ExceptionCode.EXCEPTION_CODE_LOAD_CACHE_KEY_REPETITION);
        }

    }

    public CacheConfig getCacheConfig() {
        return cacheConfig;
    }

    public CacheManager setCacheConfig(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
        return this;
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    public CacheManager setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        return this;
    }

    public void init() {
        GuavaCacheHolder guavaCacheHolder = new GuavaCacheHolder(cacheConfig, null);
        RedisCacheHolder redisCacheHolder = new RedisCacheHolder(guavaCacheHolder, redissonClient);
        this.cacheHolder = redisCacheHolder;
    }

    public Object get(String name, Key key) {

        if(!enableCache()) {
            return null;
        }

        CacheKey cacheKey = cacheKeyMap.get(name);
        if(cacheKey == null) {
            return null;
        }

        CacheObject cacheObject = (CacheObject) cacheHolder.get(cacheKey, key);
        if(cacheObject == null) {
            return null;
        }

        return cacheObject.getCacheObject();
    }

    public void put(String name, Object value, Key key) {

        if(!enableCache()) {
            return;
        }

        CacheKey cacheKey = cacheKeyMap.get(name);
        cacheHolder.put(cacheKey, key, value);

    }

    private boolean enableCache() {

        if(cacheConfig == null) {
            return false;
        }
        return cacheConfig.open();

    }

}