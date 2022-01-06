package org.wyx.diego.pontifex.cache;

/**
 * @author wangyingxin
 * @title: BaseCacheHolder
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/31
 */
public abstract class BaseCacheHolder implements CacheHolder {

    protected CacheHolder parent;

    public BaseCacheHolder() {
    }

    protected CacheHolder getParent() {
        return this.parent;
    }

    protected BaseCacheHolder setParent(CacheHolder parent) {
        this.parent = parent;
        return this;
    }

    public void put(String key, Object object) {
    }

}
