package org.wyx.diego.pontifex.loader.handler.invoke;

import org.wyx.diego.pontifex.loader.runtime.TaskRuntimeObject;
import org.wyx.diego.pontifex.pipeline.TaskContext;

public class TaskPreInvoker extends AbstractInvoker<TaskPreInvoker.Context> {

    public TaskPreInvoker(Invoker invoker) {
        super(invoker);
    }

    @Override
    public TaskPreInvoker.Context before(InvokerParam invokerParam) {

        TaskContext taskContext = invokerParam.getTaskContext();
        taskContext.setTaskRuntimeObject((TaskRuntimeObject) invokerParam.getRuntimeObject());
        return new Context().setTaskContext(taskContext);
    }

    @Override
    public void after(TaskPreInvoker.Context context) {
        TaskContext taskContext = context.getTaskContext();
        taskContext.setTaskRuntimeObject(null);
    }

    public static class Context extends org.wyx.diego.pontifex.loader.handler.invoke.Context {

        private TaskContext taskContext;

        public TaskContext getTaskContext() {
            return taskContext;
        }

        public Context setTaskContext(TaskContext taskContext) {
            this.taskContext = taskContext;
            return this;
        }
    }

}
