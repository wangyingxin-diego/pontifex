package org.wyx.diego.pontifex.cache;

/**
 * @author wangyingxin
 * @title: BaseCacheHolder
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/31
 */
public abstract class BaseCacheHolder implements CacheHolder {

    protected static final CacheObject exceptionResult = new CacheObject(null, 0);
    protected CacheHolder parent;

    public BaseCacheHolder() {
    }

    protected abstract Object get(String key);
    protected abstract void put(String getKey, Object object);

    protected abstract void put(String getKey, Object object, long timeout);

    protected abstract void delete(String key);

    protected abstract Target getTarget();

    protected CacheHolder getParent() {
        return this.parent;
    }

    protected BaseCacheHolder setParent(CacheHolder parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public Object get(CacheKey cacheKey, Key key) {

        CacheBean cacheBean = cacheKey.getCacheBean();
        if(cacheBean == null || !cacheBean.isOpen()) {
            return null;
        }

        CacheHolder parent = getParent();
        CacheObject object = null;
        if(parent != null) {
            object = (CacheObject)parent.get(cacheKey, key);
        }
        if(object == exceptionResult) {
            object = null;
        }
        if(object != null) {
            return object;
        }
        boolean contain = cacheBean.getTargetSet().contains(getTarget());
        if(!contain) {
            return null;
        }
        GetKey getKey = cacheKey.getGetKey();
        String keyStr = getKey.getKey(key);
        object = (CacheObject)get(keyStr);
        boolean valid = CacheBean.validCache(object, cacheKey);
        if(!valid) {
            return null;
        }

        return object;

    }

    @Override
    public void put(CacheKey cacheKey, Key key, Object value) {

        CacheBean cacheBean = cacheKey.getCacheBean();
        if(cacheBean == null || !cacheBean.isOpen()) {
            return;
        }

        CacheHolder parent = getParent();
        CacheObject cacheObject = new CacheObject(value, System.currentTimeMillis());
        if(parent != null) {
            parent.put(cacheKey, key, cacheObject);
        }
        GetKey getKey = cacheKey.getGetKey();
        String keyStr = getKey.getKey(key);

        put(keyStr, cacheObject, cacheBean.getTimeout());

    }
}
