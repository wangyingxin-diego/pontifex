package org.wyx.diego.pontifex.cache;

import java.io.Serializable;

public class CacheObject<T> implements Serializable {

    private T object;

    private long createTime;

    private long updateTime;

    public CacheObject(T object, long createTime) {
        this.object = object;
        this.createTime = createTime;
        this.updateTime = createTime;
    }

    public T getCacheObject() {
        return object;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public CacheObject<T> setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
