package org.wyx.diego.pontifex.loader.handler.invoke;

import org.wyx.diego.pontifex.cache.CacheBean;
import org.wyx.diego.pontifex.cache.Target;
import org.wyx.diego.pontifex.component.ComponentReq;
import org.wyx.diego.pontifex.loader.runtime.Cache;
import org.wyx.diego.pontifex.loader.runtime.ComponentRuntimeObject;

import java.util.List;

public class ComponentCacheInvoker extends AbstractInvoker<ComponentCacheInvoker.Context> {

    private static final String KEY_PRE = "pontifex_component_";

    public ComponentCacheInvoker(Invoker invoker) {
        super(invoker);
    }

    @Override
    public Context before(InvokerParam invokerParam) {

        ComponentRuntimeObject componentRuntimeObject = (ComponentRuntimeObject) invokerParam.getRuntimeObject();
        CacheBean cacheBean = componentRuntimeObject.getCache();
        Context context = new Context();
        if(!cacheBean.isOpen()) {
            return context;
        }
        List<Target> targets = cacheBean.getTargets();
        ComponentReq componentReq = (ComponentReq) invokerParam.getArgs()[0];







        return null;
    }

    @Override
    public void after(Context context) {

    }

    public static class Context extends org.wyx.diego.pontifex.loader.handler.invoke.Context {



    }

}
