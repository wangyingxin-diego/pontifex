package org.wyx.diego.pontifex;

import java.io.Serializable;

public class Pagination implements Serializable {

    //上页起始游标
    private long last;

    //下页终止游标
    private long next;

    //单页条数
    private int limit;

    //总数
    private long total;

    public long getLast() {
        return last;
    }

    public Pagination setLast(long last) {
        this.last = last;
        return this;
    }

    public long getNext() {
        return next;
    }

    public Pagination setNext(long next) {
        this.next = next;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public Pagination setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public Pagination setTotal(long total) {
        this.total = total;
        return this;
    }
}
