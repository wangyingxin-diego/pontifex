package org.wyx.diego.pontifex.component;

import java.util.concurrent.TimeUnit;

/**
 * @author wangyingxin
 * @title: Res2
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/1
 */
public class Res2<R1, R2> extends Res1<R1> {

    protected volatile R2 r2;

    public Res2(R1 r1, R2 r2) {
        super(r1);
        this.r2 = r2;
    }

    public R2 getR2() {
        return this.r2;
    }

    public R2 getR2(long timeout, TimeUnit unit) {
        return this.r2;
    }

}
