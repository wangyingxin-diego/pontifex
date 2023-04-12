package org.wyx.diego.pontifex.component;

import java.util.function.Function;

/**
 * @author wangyingxin
 * @title: SupplyAsyncComponet
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/1
 */
public abstract class SupplyAsyncComponet<P extends BaseComponentReq, R, R2, F extends Function<R, R2>> extends BaseComponent<P, R> {
    public SupplyAsyncComponet() {
    }

    abstract R2 call2(P p, Function<R, R2> function);
}
