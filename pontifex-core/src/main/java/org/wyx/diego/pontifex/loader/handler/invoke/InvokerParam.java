package org.wyx.diego.pontifex.loader.handler.invoke;


import org.wyx.diego.pontifex.loader.runtime.RuntimeObject;
import org.wyx.diego.pontifex.pipeline.TaskContext;

import java.lang.reflect.Method;

/**
 * @author diego
 * @time 2015-10-23
 * @description
 */
public class InvokerParam {

    private Object proxy;
    private Method method;
    private Object[] args;
    private Object proxyed;
    private RuntimeObject runtimeObject;
    private InvokerContext invokerContext;
    private TaskContext taskContext;

    public InvokerParam() {
    }

    public Object getProxy() {
        return this.proxy;
    }

    public InvokerParam setProxy(Object proxy) {
        this.proxy = proxy;
        return this;
    }

    public Method getMethod() {
        return this.method;
    }

    public InvokerParam setMethod(Method method) {
        this.method = method;
        return this;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public InvokerParam setArgs(Object[] args) {
        this.args = args;
        return this;
    }

    public Object getProxyed() {
        return this.proxyed;
    }

    public InvokerParam setProxyed(Object proxyed) {
        this.proxyed = proxyed;
        return this;
    }

    public InvokerContext getInvokerContext() {
        return this.invokerContext;
    }

    public InvokerParam setInvokerContext(InvokerContext invokerContext) {
        this.invokerContext = invokerContext;
        return this;
    }

    public TaskContext getTaskContext() {
        return this.taskContext;
    }

    public InvokerParam setTaskContext(TaskContext taskContext) {
        this.taskContext = taskContext;
        return this;
    }

    public RuntimeObject getRuntimeObject() {
        return this.runtimeObject;
    }

    public InvokerParam setRuntimeObject(RuntimeObject runtimeObject) {
        this.runtimeObject = runtimeObject;
        return this;
    }
}
