package org.wyx.diego.pontifex.loader.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.loader.handler.invoke.InvokerParam;
import org.wyx.diego.pontifex.loader.handler.invoke.TaskPreInvoker;
import org.wyx.diego.pontifex.pipeline.TaskContext;
import org.wyx.diego.pontifex.loader.handler.invoke.Invoker;
import org.wyx.diego.pontifex.loader.handler.invoke.TaskLogInvoker;
import org.wyx.diego.pontifex.loader.runtime.TaskRuntimeObject;
import org.wyx.diego.pontifex.pipeline.PLTask;
import org.wyx.diego.pontifex.pipeline.Task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author diego
 * @time 2015-10-10
 * @description
 */
public class TaskInvocationHandler extends AbstractInvocationHandler<PLTask, TaskRuntimeObject> {

private static final Logger logger = LoggerFactory.getLogger(TaskInvocationHandler.class);
private static final Map<Method, Boolean> methodNameMap = new ConcurrentHashMap<>();
private static Method METHOD;

public TaskInvocationHandler(Task proxyed, TaskRuntimeObject runtimeObject) {
        super(runtimeObject, new TaskPreInvoker(new TaskLogInvoker(new TaskInvocationHandler.TaskInvoker(proxyed))), proxyed);
}

static {
    Method[] methods = PLTask.class.getDeclaredMethods();
    for(Method method : methods) {
        String methodName = method.getName();
        if (methodName == "run") {
            METHOD = method;
        }
    }
}

private static class TaskInvoker implements Invoker {
    private final Task proxyed;

    public TaskInvoker(Task proxyed) {
        this.proxyed = proxyed;
    }

    public Object before(InvokerParam invokerParam) {
        Method method = invokerParam.getMethod();
        Object proxy = invokerParam.getProxy();
        Object[] args = invokerParam.getArgs();
        if (!method.getName().equals("run")) {
            TaskContext taskContext = (TaskContext)args[0];
        }

        return invokerParam;
    }

    public Object invoke(InvokerParam invokerParam) {
        Method method = invokerParam.getMethod();
        Object proxy = invokerParam.getProxy();
        Object[] args = invokerParam.getArgs();
        if (!method.getName().equals("run")) {
            try {
                Object object = method.invoke(this.proxyed, args);
                return object;
            } catch (IllegalAccessException illegalAccessException) {
                throw new RuntimeException(illegalAccessException);
            } catch (InvocationTargetException invocationTargetException) {
                throw new RuntimeException(invocationTargetException);
            }
        } else {
            this.proxyed.run((TaskContext)args[0]);
            return null;
        }
    }

    public void after(Object o) {
    }
}

    @Override
    boolean methodHandled(Method method) {
        if(method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == METHOD.getParameterTypes()[0]) {
            return true;
        }
        return false;
    }
}
