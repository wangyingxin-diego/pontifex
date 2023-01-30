package org.wyx.diego.pontifex.loader.handler;


import org.wyx.diego.pontifex.loader.handler.invoke.Invoker;
import org.wyx.diego.pontifex.loader.handler.invoke.InvokerParam;
import org.wyx.diego.pontifex.loader.handler.invoke.LogInvoker;
import org.wyx.diego.pontifex.loader.runtime.RuntimeObject;
import org.wyx.diego.pontifex.pipeline.TaskContext;
import org.wyx.diego.pontifex.util.ThreadLocalUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author diego
 * @time 2015-10-23
 * @description
 */
abstract class AbstractInvocationHandler<P, Runtime extends RuntimeObject> implements InvocationHandler {
    protected Runtime runtimeObject;
    protected Invoker invoker;
    protected P proxyed;

    protected AbstractInvocationHandler(Runtime runtime, Invoker invoker, P proxyed) {
        this.runtimeObject = runtime;
        this.proxyed = proxyed;
        this.invoker = invoker;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TaskContext taskContext = ThreadLocalUtil.get();
        InvokerParam invokerParam = new InvokerParam();
        invokerParam.setArgs(args).setMethod(method).setProxy(proxy).setProxyed(this.proxyed);
        invokerParam.setRuntimeObject(this.runtimeObject);
        invokerParam.setTaskContext(taskContext);
        Object object = this.invoker.invoke(invokerParam);
        return object;
    }

    public Runtime getRuntimeObject() {
        return this.runtimeObject;
    }

    public Invoker getInvoker() {
        return this.invoker;
    }

    public P getProxyed() {
        return this.proxyed;
    }
}
