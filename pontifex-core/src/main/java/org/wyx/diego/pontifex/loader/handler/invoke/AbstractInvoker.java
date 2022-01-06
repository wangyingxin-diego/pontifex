package org.wyx.diego.pontifex.loader.handler.invoke;

/**
 * @author diego
 * @time 2015-10-23
 * @description
 */
public abstract class AbstractInvoker<K> implements Invoker<K>{

    private Invoker invoker;

    public AbstractInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public Object invoke(InvokerParam invokerParam) {

        K context = before(invokerParam);
        Object object = invoker.invoke(invokerParam);
        after(context);

        return object;

    }
}
