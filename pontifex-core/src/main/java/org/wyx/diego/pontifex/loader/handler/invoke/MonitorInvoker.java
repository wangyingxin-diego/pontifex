package org.wyx.diego.pontifex.loader.handler.invoke;

/**
 * @author diego
 * @time 2015-09-13
 * @description
 */
public class MonitorInvoker extends AbstractInvoker<InvokerParam> {

    public MonitorInvoker(Invoker invoker) {
        super(invoker);
    }

    @Override
    public InvokerParam before(InvokerParam invokerParam) {
        return invokerParam;
    }

    @Override
    public void after(InvokerParam invokerParam) {

    }
}
