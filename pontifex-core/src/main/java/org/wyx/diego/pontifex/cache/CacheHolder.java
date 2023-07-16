package org.wyx.diego.pontifex.cache;

/**
 * @author wangyingxin
 * @title: CacheHolder
 * @projectName pontifex
 * @description: TODO
 * @date 2015/10/31
 */
public interface CacheHolder {

    Object get(CacheKey cacheKey, Key key);

    void put(CacheKey cacheKey, Key key, Object value);

}
