package org.wyx.diego.pontifex.component;

import java.util.function.Function;

/**
 * @author wangyingxin
 * @title: SupplyAsyncComponet
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/1
 */
public abstract class SupplyAsyncComponet<P extends BaseComponentReq, R extends BaseComponentRes, R2 extends BaseComponentRes, F extends Function<R, R2>> extends BaseComponent<P, R> {
    public SupplyAsyncComponet() {
    }

    abstract R2 call2(P var1, Function<R, R2> var2);
}
