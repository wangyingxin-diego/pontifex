package org.wyx.diego.pontifex.loader.runtime;

/**
 * @author wangyingxin
 * @title: Async
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/31
 */
public class Async {

    private final boolean isOpen;
    private final short concurrency;

    public Async(boolean isOpen, short concurrency) {
        this.isOpen = isOpen;
        this.concurrency = concurrency;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public short getConcurrency() {
        return this.concurrency;
    }

}
