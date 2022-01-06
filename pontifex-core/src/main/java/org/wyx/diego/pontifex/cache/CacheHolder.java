package org.wyx.diego.pontifex.cache;

/**
 * @author wangyingxin
 * @title: CacheHolder
 * @projectName pontifex
 * @description: TODO
 * @date 2015/10/31
 */
public interface CacheHolder {

    Object get(String key);

    void put(String key, Object value);

}
