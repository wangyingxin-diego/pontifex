package org.wyx.diego.pontifex.loader.handler.invoke;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.component.ComponentReq;
import org.wyx.diego.pontifex.loader.runtime.ComponentRuntimeObject;
import org.wyx.diego.pontifex.loader.runtime.TaskRuntimeObject;
import org.wyx.diego.pontifex.pipeline.TaskContext;
import org.wyx.diego.pontifex.util.ThreadLocalUtil;

public class ComponentLogInvoker extends AbstractInvoker<LogInvoker.Context>  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentLogInvoker.class);

    public ComponentLogInvoker(Invoker invoker) {
        super(invoker);
    }

    @Override
    public LogInvoker.Context before(InvokerParam invokerParam) {

        long startTime = System.currentTimeMillis();
        LogInvoker.Context context = new LogInvoker.Context();
        context.setStartTime(startTime).setInvokerParam(invokerParam);
        logStart(invokerParam);
        return context;

    }

    @Override
    public void after(LogInvoker.Context context) {

        InvokerParam invokerParam = context.getInvokerParam();
        ComponentRuntimeObject runtimeObject = (ComponentRuntimeObject) invokerParam.getRuntimeObject();
        long endTime = System.currentTimeMillis();
        long spend = endTime - context.getStartTime();
        TaskContext taskContext = ThreadLocalUtil.get();
        LogTaskContext logTaskContext = taskContext.getLogTaskContext4Component();

//        String spend0;
//        if (logTaskContext != null) {
//            spend0 = logTaskContext.toString();
//        } else {
//            spend0 = String.valueOf(spend);
//        }
        String pipelineName = taskContext.getTaskRuntimeObject().getPipelineName();
        Object result = context.getObject();
        if (runtimeObject.getTimeout() > spend) {
            LOGGER.warn("pontifex pipeline={}  module name={} invoke end, spend={}, result={}", pipelineName, runtimeObject.getName(), spend, JSONObject.toJSONString(result));
        } else {
            LOGGER.info("pontifex pipeline={}  module name={} invoke end, spend={}, result={}", pipelineName, runtimeObject.getName(), spend, JSONObject.toJSONString(result));
        }

        LogTask logTask = new LogTask();
        logTask.setName(runtimeObject.getName()).setSpend(spend);
        logTaskContext.addLogTask(logTask);
    }

    private void logStart(InvokerParam invokerParam) {

        Object[] args = invokerParam.getArgs();
        if (args.length < 1) {
            LOGGER.info("pontifex module name={} invoke start, param={}", invokerParam.getProxyed().getClass().getSimpleName(), "");
            return;
        }
        Object object = args[0];
        if(!(object instanceof ComponentReq)) {
            LOGGER.info("pontifex module name={} invoke start, param={}", invokerParam.getProxyed().getClass().getSimpleName());
        }

        ComponentReq componentReq = (ComponentReq)object;
        TaskContext taskContext = invokerParam.getTaskContext();
        TaskRuntimeObject taskRuntimeObject = taskContext.getTaskRuntimeObject();
        ComponentRuntimeObject componentRuntimeObject = (ComponentRuntimeObject) invokerParam.getRuntimeObject();

        LOGGER.info("pontifex pipelineName={} taskName={} module name={} invoke start, ComponentReq={}", taskRuntimeObject.getPipelineName(), taskRuntimeObject.getName(), componentRuntimeObject.getName(), JSONObject.toJSONString(componentReq));

    }


}
