package org.wyx.diego.pontifex.component;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author wangyingxin
 * @title: Res1
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/1
 */
public class Res1<R1> implements Serializable {

    protected volatile R1 r1;

    public Res1(R1 r1) {
        this.r1 = r1;
    }

    public R1 getR1() {
        return this.r1;
    }

    public R1 getR1(long timeout, TimeUnit unit) {
        return this.r1;
    }

}
