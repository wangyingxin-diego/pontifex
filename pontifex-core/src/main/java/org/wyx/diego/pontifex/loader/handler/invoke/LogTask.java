package org.wyx.diego.pontifex.loader.handler.invoke;

/**
 * @author diego
 * @time 2015-10-24
 * @description
 */
public class LogTask {
    private static final String TEMPLATE = "[%s:%s]";
    private String name;
    private long spend;

    public LogTask() {
    }

    public String getName() {
        return this.name;
    }

    public LogTask setName(String name) {
        this.name = name;
        return this;
    }

    public long getSpend() {
        return this.spend;
    }

    public LogTask setSpend(long spend) {
        this.spend = spend;
        return this;
    }

    public String toString() {
        return String.format(TEMPLATE, this.name, this.spend);
    }
}
