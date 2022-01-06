package org.wyx.diego.pontifex.component;

/**
 * @author diego
 * @time 2015-10-25
 * @description
 */
public class BaseComponentReq implements ComponentReq {
    private boolean sync = false;

    public BaseComponentReq() {
    }

    public boolean isSync() {
        return this.sync;
    }

    public BaseComponentReq setSync(boolean sync) {
        this.sync = sync;
        return this;
    }
}
