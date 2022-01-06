package org.wyx.diego.pontifex.loader.handler.invoke;

/**
 * @author diego
 * @time 2015-10-23
 * @description
 */
public interface Invoker<K> {

    K before(InvokerParam invokerParam);

    Object invoke(InvokerParam invokerParam);

    void after(K k);

}
