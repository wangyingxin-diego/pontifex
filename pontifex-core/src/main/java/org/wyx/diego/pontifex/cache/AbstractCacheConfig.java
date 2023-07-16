package org.wyx.diego.pontifex.cache;

public abstract class AbstractCacheConfig implements CacheConfig {

    private boolean open;

    private long maxMemorySize;

    private long timeout;

    public boolean isOpen() {
        return open;
    }

    public AbstractCacheConfig setOpen(boolean open) {
        this.open = open;
        return this;
    }

    public long getMaxMemorySize() {
        return maxMemorySize;
    }

    public AbstractCacheConfig setMaxMemorySize(long maxMemorySize) {
        this.maxMemorySize = maxMemorySize;
        return this;
    }

    public long getTimeout() {
        return timeout;
    }

    public AbstractCacheConfig setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }
}
