package org.wyx.diego.pontifex.loader.handler.invoke;

import org.wyx.diego.pontifex.component.ComponentReq;
import org.wyx.diego.pontifex.pipeline.TaskContext;

/**
 * @author diego
 * @time 2015-10-23
 * @description
 */
public abstract class AbstractInvoker<K extends Context> implements Invoker<K>{

    private Invoker invoker;

    public AbstractInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public Object invoke(InvokerParam invokerParam) {

        Object[] objects = invokerParam.getArgs();
        if(objects == null || objects.length < 1 || !(objects[0] instanceof TaskContext || objects[0] instanceof ComponentReq)) {
            return invoker.invoke(invokerParam);
        }

        K context = before(invokerParam);
        Object object = invoker.invoke(invokerParam);
        context.setObject(object);
        after(context);

        return object;

    }
}
