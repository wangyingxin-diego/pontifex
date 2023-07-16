package org.wyx.diego.pontifex.loader.handler.invoke;

/**
 * @author diego
 * @time 2015-03-13
 * @description
 */
public class InvokerContext {

    private long startTime;
    private long endTime;
    private boolean sync = false;

    private boolean pass = false;

    private Object result = null;

    public InvokerContext() {
    }

    public long getStartTime() {
        return this.startTime;
    }

    public InvokerContext setStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public InvokerContext setEndTime(long endTime) {
        this.endTime = endTime;
        return this;
    }

    public boolean isSync() {
        return this.sync;
    }

    public InvokerContext setSync(boolean sync) {
        this.sync = sync;
        return this;
    }

    public boolean isPass() {
        return pass;
    }

    public InvokerContext setPass(boolean pass) {
        this.pass = pass;
        return this;
    }

    public Object getResult() {
        return result;
    }

    public InvokerContext setResult(Object result) {
        this.result = result;
        return this;
    }
}
