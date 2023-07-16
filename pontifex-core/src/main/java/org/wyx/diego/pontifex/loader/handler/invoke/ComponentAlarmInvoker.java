package org.wyx.diego.pontifex.loader.handler.invoke;

import org.wyx.diego.pontifex.annotation.Alarm;
import org.wyx.diego.pontifex.loader.runtime.ComponentRuntimeObject;

import java.util.List;

public class ComponentAlarmInvoker extends AbstractInvoker<ComponentAlarmInvoker.Context> {

    public ComponentAlarmInvoker(Invoker invoker) {
        super(invoker);
    }

    @Override
    public Context before(InvokerParam invokerParam) {
        Context context = new Context();
        context.setInvokerParam(invokerParam);
        return context;
    }

    @Override
    public void after(Context context) {

        InvokerParam invokerParam = context.getInvokerParam();
        InvokerContext invokerContext = invokerParam.getInvokerContext();
        long startTime = invokerContext.getStartTime();
        long endTime = invokerContext.getEndTime();
        ComponentRuntimeObject componentRuntimeObject = (ComponentRuntimeObject) invokerParam.getRuntimeObject();
        List<Alarm> alarms = componentRuntimeObject.getAlarms();
        if(alarms == null || alarms.size() == 0) {
            return;
        }



    }

    static class Context extends org.wyx.diego.pontifex.loader.handler.invoke.Context {
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
