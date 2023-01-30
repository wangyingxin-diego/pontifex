package org.wyx.diego.pontifex.loader.handler.invoke;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.loader.runtime.RuntimeObject;
import org.wyx.diego.pontifex.loader.runtime.TaskRuntimeObject;
import org.wyx.diego.pontifex.pipeline.PLTask;
import org.wyx.diego.pontifex.pipeline.TaskContext;
import org.wyx.diego.pontifex.util.ThreadLocalUtil;

public class TaskLogInvoker extends AbstractInvoker<LogInvoker.Context> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskLogInvoker.class);

    public TaskLogInvoker(Invoker invoker) {
        super(invoker);
    }

    @Override
    public LogInvoker.Context before(InvokerParam invokerParam) {

        this.logStart(invokerParam);
        RuntimeObject runtimeObject = invokerParam.getRuntimeObject();
        long startTime = System.currentTimeMillis();

        LogInvoker.Context context = new LogInvoker.Context();
        LogTaskContext logTaskContext = new LogTaskContext();
        TaskContext taskContext = ThreadLocalUtil.get();
        taskContext.setLogTaskContext4Component(logTaskContext);
        context.setStartTime(startTime).setInvokerParam(invokerParam);
        return context;

    }

    @Override
    public void after(LogInvoker.Context context) {

        InvokerParam invokerParam = context.getInvokerParam();
        TaskRuntimeObject runtimeObject = (TaskRuntimeObject) invokerParam.getRuntimeObject();
        long spend = System.currentTimeMillis() - context.getStartTime();
        TaskContext taskContext4Com = ThreadLocalUtil.get();
        LogTaskContext logTaskContext = taskContext4Com.getLogTaskContext4Component();

        String spend0;
        if (logTaskContext != null) {
            spend0 = logTaskContext.toString();
        } else {
            spend0 = String.valueOf(spend);
        }
        if(spend0 == null || "".equals(spend0.trim())) {
            spend0 = "[]";
        }
        String pipelineName = runtimeObject.getPipelineName();
        if (runtimeObject.getTimeout() > spend) {
            LOGGER.warn("pontifex pipeline={} module name={} invoke end, spend={}, spend0={}", pipelineName, runtimeObject.getName(), spend, spend0);
        } else {
            LOGGER.info("pontifex pipeline={} module name={} invoke end, spend={}, spend0={}", pipelineName, runtimeObject.getName(), spend, spend0);
        }

        TaskContext taskContext = (TaskContext)invokerParam.getArgs()[0];
        LogTask logTask = new LogTask();
        logTask.setName(runtimeObject.getName()).setSpend(spend);
        taskContext.getLogTaskContext().addLogTask(logTask);
    }

    private void logStart(InvokerParam invokerParam) {
        Object[] args = invokerParam.getArgs();
        TaskRuntimeObject taskRuntimeObject = (TaskRuntimeObject) invokerParam.getRuntimeObject();
        String pipelineName = taskRuntimeObject.getPipelineName();
        int innerSort = ((PLTask)invokerParam.getProxyed()).getInnerSort();
        String name = taskRuntimeObject.getName();
        if (args.length < 1) {
            LOGGER.info("pontifex pipeline={} module name={} invoke start, param={}", pipelineName, name, "");
            return;
        }
        TaskContext taskContext = (TaskContext) args[0];
        name = name + "-" + innerSort;
        if(innerSort == 0) {
            LOGGER.info("pontifex pipeline={} module name={} invoke start, request={}, payload={}, response={}", pipelineName, name, JSONObject.toJSONString(taskContext.getPontifexRequest()), JSONObject.toJSONString(taskContext.getPayload()), JSONObject.toJSONString(taskContext.getPontifexResponse()));
        } else {
            LOGGER.info("pontifex pipeline={} module name={} invoke start, payload={}, response={}", pipelineName, name, JSONObject.toJSONString(taskContext.getPayload()), JSONObject.toJSONString(taskContext.getPontifexResponse()));
        }
//        LOGGER.info("pontifex pipeline={} module name={} invoke start, request={}, payload={}, response={}", pipelineName, name, JSONObject.toJSONString(taskContext.getPontifexRequest()), JSONObject.toJSONString(taskContext.getPayload()), JSONObject.toJSONString(taskContext.getPontifexResponse()));

    }

}
