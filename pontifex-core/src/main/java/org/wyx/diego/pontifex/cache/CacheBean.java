package org.wyx.diego.pontifex.cache;

import com.sun.javafx.collections.UnmodifiableListSet;
import org.wyx.diego.pontifex.annotation.Cache;

import java.util.*;

/**
 * @author wangyingxin
 * @title: CacheBean
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/31
 */
public class CacheBean {

    private boolean open;
    private List<Target> targets = new ArrayList();
    private long timeout;
    private long maximumSize;

    private Set<Target> targetSet;

    public CacheBean(Cache cache) {
        this.open = cache.isOpen();
        this.timeout = cache.timeout();
        this.maximumSize = cache.maximumSize();
        Arrays.sort(cache.target());
        Collections.addAll(this.targets, cache.target());
        targetSet = new UnmodifiableListSet<>(targets);
    }

    public boolean isOpen() {
        return this.open;
    }

    public CacheBean setOpen(boolean open) {
        this.open = open;
        return this;
    }

    public List<Target> getTargets() {
        return this.targets;
    }

    public CacheBean setTargets(List<Target> targets) {
        this.targets = targets;
        return this;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public CacheBean setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public long getMaximumSize() {
        return this.maximumSize;
    }

    public CacheBean setMaximumSize(long maximumSize) {
        this.maximumSize = maximumSize;
        return this;
    }

    public Set<Target> getTargetSet() {
        return targetSet;
    }

    public static boolean validCache(CacheObject cacheObject, CacheKey cacheKey) {

        if(cacheObject == null || cacheObject.getCacheObject() == null) {
            return false;
        }
        long updateTime = cacheObject.getUpdateTime();
        long now = System.currentTimeMillis();
        long d = now - updateTime;
        long validTime = cacheKey.getCacheBean().timeout;
        if(d > validTime) {
            return false;
        }
        return true;

    }
}
