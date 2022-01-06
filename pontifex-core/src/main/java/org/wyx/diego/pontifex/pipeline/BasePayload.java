package org.wyx.diego.pontifex.pipeline;

/**
 * @author wangyingxin
 * @title: BasePayload
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/3
 */
public abstract class BasePayload implements Payload {
    private boolean forceCache = true;

    public BasePayload() {
    }

    public boolean isForceCache() {
        return this.forceCache;
    }

    public BasePayload setForceCache(boolean forceCache) {
        this.forceCache = forceCache;
        return this;
    }
}
