package org.wyx.diego.pontifex.loader.handler.invoke;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.ModuleType;
import org.wyx.diego.pontifex.component.ComponentReq;
import org.wyx.diego.pontifex.loader.runtime.RuntimeObject;
import org.wyx.diego.pontifex.pipeline.TaskContext;
import org.wyx.diego.pontifex.util.ThreadLocalUtil;

/**
 * @author diego
 * @time 2015-10-23
 * @description
 */
public class LogInvoker extends AbstractInvoker<LogInvoker.Context> {
    private static final Logger logger = LoggerFactory.getLogger(LogInvoker.class);

    public LogInvoker(Invoker invoker) {
        super(invoker);
    }

    public LogInvoker.Context before(InvokerParam invokerParam) {
        this.logBefore(invokerParam);
        RuntimeObject runtimeObject = invokerParam.getRuntimeObject();
        long startTime = 0L;
        if (runtimeObject != null) {
            startTime = System.currentTimeMillis();
        }

        ModuleType moduleType = runtimeObject.getModuleType();
        LogInvoker.Context context = new LogInvoker.Context();
        if (moduleType == ModuleType.MODULE_TYPE_TASK && invokerParam.getArgs().length > 0 && invokerParam.getArgs()[0] instanceof TaskContext) {
            LogTaskContext logTaskContext = new LogTaskContext();
            TaskContext taskContext = ThreadLocalUtil.get();
            if (taskContext != null) {
                taskContext.setLogTaskContext4Component(logTaskContext);
            }
        }

        context.setStartTime(startTime).setInvokerParam(invokerParam);
        return context;
    }

    public void after(LogInvoker.Context context) {
        InvokerParam invokerParam = context.getInvokerParam();
        RuntimeObject runtimeObject = invokerParam.getRuntimeObject();
        long endTime = System.currentTimeMillis();
        long spend = endTime - context.getStartTime();
        TaskContext taskContext4Com = ThreadLocalUtil.get();
        LogTaskContext logTaskContext = null;
        if (taskContext4Com != null) {
            logTaskContext = taskContext4Com.getLogTaskContext4Component();
        }

        String spend0;
        if (logTaskContext != null) {
            spend0 = logTaskContext.toString();
        } else {
            spend0 = String.valueOf(spend);
        }

        if (runtimeObject.getTimeout() > spend) {
            logger.warn("pontifex module name={} invoke end, spend={}, spend0={}", new Object[]{context.getInvokerParam().getProxyed().getClass().getSimpleName(), spend, spend0});
        } else {
            logger.info("pontifex module name={} invoke end, spend={}, spend0={}", new Object[]{context.getInvokerParam().getProxyed().getClass().getSimpleName(), spend, spend0});
        }

        ModuleType moduleType = runtimeObject.getModuleType();
        TaskContext taskContext;
        if (moduleType == ModuleType.MODULE_TYPE_TASK && invokerParam.getArgs().length > 0 && invokerParam.getArgs()[0] instanceof TaskContext) {
            taskContext = (TaskContext)invokerParam.getArgs()[0];
            LogTask logTask = new LogTask();
            logTask.setName(context.getInvokerParam().getProxyed().getClass().getSimpleName()).setSpend(spend);
            taskContext.getLogTaskContext().addLogTask(logTask);
        } else if (moduleType == ModuleType.MODULE_TYPE_COMPONENT && invokerParam.getArgs().length > 0 && invokerParam.getArgs()[0] instanceof ComponentReq) {
            taskContext = ThreadLocalUtil.get();
            if (taskContext != null) {
                LogTaskContext logTaskContext4C = taskContext.getLogTaskContext4Component();
                if (logTaskContext4C != null) {
                    LogTask logTask = new LogTask();
                    logTask.setName(context.getInvokerParam().getProxyed().getClass().getSimpleName()).setSpend(spend);
                    logTaskContext4C.addLogTask(logTask);
                }
            }
        }

    }

    private void logBefore(InvokerParam invokerParam) {
        Object[] args = invokerParam.getArgs();
        if (args.length < 1) {
            logger.info("pontifex module Name={} invoke start, param={}", invokerParam.getProxyed().getClass().getSimpleName(), "");
        } else {
            Object object = args[0];
            if (object instanceof TaskContext) {
                TaskContext taskContext = (TaskContext)object;
                logger.info("pontifex module Name={} invoke start, request={}, response={}", new Object[]{invokerParam.getProxyed().getClass().getSimpleName(), JSONObject.toJSONString(taskContext.getPontifexRequest()), JSONObject.toJSONString(taskContext.getPontifexResponse())});
            } else if (object instanceof ComponentReq) {
                ComponentReq componentReq = (ComponentReq)object;
                logger.info("pontifex module Name={} invoke start, ComponentReq={}", invokerParam.getProxyed().getClass().getSimpleName(), JSONObject.toJSONString(componentReq));
            } else {
                logger.info("pontifex module Name={} invoke start, param={}", invokerParam.getProxyed().getClass().getSimpleName());
            }
        }
    }

    public static class Context {
        private InvokerParam invokerParam;
        private long startTime;
        private LogTaskContext logTaskContext;

        public Context() {
        }

        public InvokerParam getInvokerParam() {
            return this.invokerParam;
        }

        public LogInvoker.Context setInvokerParam(InvokerParam invokerParam) {
            this.invokerParam = invokerParam;
            return this;
        }

        public long getStartTime() {
            return this.startTime;
        }

        public LogInvoker.Context setStartTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        public LogTaskContext getLogTaskContext() {
            return this.logTaskContext;
        }

        public LogInvoker.Context setLogTaskContext(LogTaskContext logTaskContext) {
            this.logTaskContext = logTaskContext;
            return this;
        }
    }
}
