package org.wyx.diego.pontifex.loader.handler.invoke;

/**
 * @author diego
 * @time 2015-09-13
 * @description
 */
public class MonitorInvoker extends AbstractInvoker<Context> {

    public MonitorInvoker(Invoker invoker) {
        super(invoker);
    }

    @Override
    public org.wyx.diego.pontifex.loader.handler.invoke.Context before(InvokerParam invokerParam) {
        return null;
    }

    @Override
    public void after(org.wyx.diego.pontifex.loader.handler.invoke.Context context) {

    }

    public static final class Context extends org.wyx.diego.pontifex.loader.handler.invoke.Context {

    }

}
