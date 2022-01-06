package org.wyx.diego.pontifex.pipeline;

import java.util.List;

/**
 * @author diego
 * @time 2016-03-11
 * @description
 */
public class ProxyedTask extends Task {

    private PLTask plTask;

    public ProxyedTask(PLTask plTask) {
        this.plTask = plTask;
    }

    @Override
    public void run(PLContext ctx) {
        this.plTask.run(ctx);
    }

    @Override
    public List<String> keyNames() {
        return this.plTask.keyNames();
    }

    @Override
    public int getType() {
        return this.plTask.getType();
    }

    public PLTask getPlTask() {
        return plTask;
    }
}
