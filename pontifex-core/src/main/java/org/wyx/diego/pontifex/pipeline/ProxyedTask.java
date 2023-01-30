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
    public int compareTo(PLTask o) {
        return plTask.compareTo(o);
    }

    @Override
    public int getType() {
        return this.plTask.getType();
    }

    public PLTask getPlTask() {
        return plTask;
    }

    @Override
    public int getInnerSort() {
        return plTask.getInnerSort();
    }

    @Override
    public void setInnerSort(int innerSort) {
        this.plTask.setInnerSort(innerSort);
        plTask.setInnerSort(innerSort);
    }
}
