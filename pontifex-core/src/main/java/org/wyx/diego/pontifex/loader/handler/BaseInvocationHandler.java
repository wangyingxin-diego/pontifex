package org.wyx.diego.pontifex.loader.handler;


import org.wyx.diego.pontifex.loader.handler.invoke.Invoker;
import org.wyx.diego.pontifex.loader.runtime.RuntimeObject;

/**
 * @author diego
 * @time 2015-10-23
 * @description
 */
abstract class BaseInvocationHandler extends AbstractInvocationHandler {
    public BaseInvocationHandler(RuntimeObject runtimeObject, Invoker invoker) {
        super(runtimeObject, invoker, (Object)null);
    }
}
