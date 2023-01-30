package org.wyx.diego.pontifex.loader.runtime;

import org.wyx.diego.pontifex.cache.Target;

import java.util.Arrays;
import java.util.List;

public class Cache {

    private boolean open;

    private List<Target> targets;

    private long timeout;

    private long maximumSize;

    public Cache(org.wyx.diego.pontifex.annotation.Cache cache) {

        this.open = cache.isOpen();
        this.targets = Arrays.asList(cache.target());
        this.timeout = cache.timeout();
        this.maximumSize = cache.maximumSize();

    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(long maximumSize) {
        this.maximumSize = maximumSize;
    }
}
