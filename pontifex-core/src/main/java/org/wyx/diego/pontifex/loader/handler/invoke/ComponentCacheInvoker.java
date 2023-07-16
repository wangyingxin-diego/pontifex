package org.wyx.diego.pontifex.loader.handler.invoke;

import org.wyx.diego.pontifex.cache.CacheManagerInstance;
import org.wyx.diego.pontifex.loader.runtime.ComponentRuntimeObject;
import org.wyx.diego.pontifex.component.BaseComponentReq;

public class ComponentCacheInvoker extends AbstractInvoker<ComponentCacheInvoker.Context> {

    public ComponentCacheInvoker(Invoker invoker) {
        super(invoker);
    }

    @Override
    public Context before(InvokerParam invokerParam) {

        ComponentRuntimeObject componentRuntimeObject = ((ComponentRuntimeObject) invokerParam.getRuntimeObject());
        Context context = new Context();
        context.setInvokerParam(invokerParam);
        if(componentRuntimeObject.getAsync().isOpen()) {
            return context;
        }
        String name = componentRuntimeObject.getName();
        BaseComponentReq baseComponentReq = (BaseComponentReq)invokerParam.getArgs()[0];
        Object object = CacheManagerInstance.INSTANCE.getCacheManager().get(name, baseComponentReq);
        if(object != null) {
            invokerParam.getInvokerContext().setPass(true).setResult(object);
        }

        return context;

    }

    @Override
    public void after(Context context) {

        InvokerParam invokerParam = context.invokerParam;
        ComponentRuntimeObject componentRuntimeObject = ((ComponentRuntimeObject) invokerParam.getRuntimeObject());
        String name = componentRuntimeObject.getName();

        if(componentRuntimeObject.getAsync().isOpen()) {
            return;
        }
        BaseComponentReq baseComponentReq = (BaseComponentReq)invokerParam.getArgs()[0];
        CacheManagerInstance.INSTANCE.getCacheManager().put(name, context.getObject(), baseComponentReq);

    }

    public static class Context extends org.wyx.diego.pontifex.loader.handler.invoke.Context {

        private InvokerParam invokerParam;

        public InvokerParam getInvokerParam() {
            return invokerParam;
        }

        public Context setInvokerParam(InvokerParam invokerParam) {
            this.invokerParam = invokerParam;
            return this;
        }
    }

}
