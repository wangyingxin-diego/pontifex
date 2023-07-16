package org.wyx.diego.pontifex.loader.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.Component;
import org.wyx.diego.pontifex.exception.ExceptionCode;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.cache.*;
import org.wyx.diego.pontifex.component.*;
import org.wyx.diego.pontifex.cache.*;
import org.wyx.diego.pontifex.component.*;
import org.wyx.diego.pontifex.loader.handler.invoke.*;
import org.wyx.diego.pontifex.loader.handler.invoke.*;
import org.wyx.diego.pontifex.loader.runtime.Async;
import org.wyx.diego.pontifex.loader.runtime.ComponentRuntimeObject;
import org.wyx.diego.pontifex.util.ThreadLocalUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author diego
 * @time 2015-10-15
 * @description
 */
public final class ComponentInvocationHandler extends AbstractInvocationHandler<Component, ComponentRuntimeObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentInvocationHandler.class);
    private static final Map<Method, Component.MethodName> methodNameMap = new ConcurrentHashMap();
    private static Method CALL;
    private static  Method APPLY;

    public ComponentInvocationHandler(ComponentInvocationHandler.ComponentProxy componentProxy) {
        super(componentProxy.componentRuntimeObject, new ComponentLogInvoker(new ComponentCacheInvoker(componentProxy.asyncComponentInvoker)), componentProxy.component);
    }

    static {

        Method[] methods = Component.class.getDeclaredMethods();
        for(Method method : methods) {
            String methodName = method.getName();
            if(methodName.equals("call")) {
                CALL = method;
                continue;
            }
            if(methodName.equals("apply")) {
                APPLY = method;
            }
        }

    }

    private static final class AsyncComponentInvoker extends ComponentInvocationHandler.ComponentInvoker {
        private final ExecutorService executorService;
        private final ComponentRuntimeObject componentRuntimeObject;
        private final boolean defaultAsync;

        public AsyncComponentInvoker(Component proxyed, ComponentRuntimeObject componentRuntimeObject) {
            super(proxyed);
            this.componentRuntimeObject = componentRuntimeObject;
            String name = this.componentRuntimeObject.getName();
            Async async = this.componentRuntimeObject.getAsync();
            short concurrency = async.getConcurrency();
            this.executorService = Executors.newFixedThreadPool(concurrency, new ComponentInvocationHandler.AsyncComponentInvoker.ComponentThreadFactory(name));
            this.defaultAsync = this.componentRuntimeObject.getAsync().isOpen();

            GetKey getKey = proxyed.getKey();
            if(getKey == null) {
                getKey = DefaultComponentGetKey.DEFAULT_COMPONENT_GET_KEY;
            }
            CacheBean cacheBean = componentRuntimeObject.getCache();
            CacheKey cacheKey = new CacheKey(getKey, cacheBean);
            CacheManager.addCacheKey(name, cacheKey);

        }

        public InvokerParam before(InvokerParam invokerParam) {
            return invokerParam;
        }

        public void after(InvokerParam invokerParam) {

        }

        List<CompletableFuture> handleCall(Method method, InternalReq internalReq, Object[] args) {
            if (this.defaultAsync && !internalReq.isSync()) {
                CompletableFuture completableFuture = CompletableFuture.supplyAsync(() -> {
                    ThreadLocalUtil.setTaskContext(internalReq.getTaskContext());

                    Res1 object;
                    try {
                        object = this.proxyed.call(internalReq.getRequest());
                    } catch (Exception e) {
                        throw e;
                    } finally {
                        ThreadLocalUtil.remove();
                    }

                    return object;
                }, this.executorService);
                List<CompletableFuture> completableFutures = new ArrayList();
                completableFutures.add(completableFuture);
                return completableFutures;
            }
            return super.handleCall(method, internalReq, args);
        }

        List<CompletableFuture> handleApply(Method method, InternalReq internalReq, Object[] args) {
            if (this.defaultAsync && !internalReq.isSync()) {
                List<CompletableFuture> completableFutures = this.handleCall(method, internalReq, args);
                CompletableFuture completableFuture4Apply = ((CompletableFuture)completableFutures.get(0)).thenApply((Function)args[1]);
                completableFutures.add(completableFuture4Apply);
                return completableFutures;
            } else {
                return super.handleApply(method, internalReq, args);
            }
        }

        private static final class ComponentThreadFactory implements ThreadFactory {
            private static final AtomicInteger poolNumber = new AtomicInteger(1);
            private final ThreadGroup group;
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            private final String namePrefix;

            ComponentThreadFactory(String name) {
                SecurityManager s = System.getSecurityManager();
                this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
                this.namePrefix = "pool-" + name + "-" + poolNumber.getAndIncrement() + "-thread-";
            }

            public Thread newThread(Runnable r) {
                Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
                if (t.isDaemon()) {
                    t.setDaemon(false);
                }

                if (t.getPriority() != 5) {
                    t.setPriority(5);
                }

                return t;
            }
        }
    }

    private static class ComponentInvoker extends ComponentInvocationHandler.AbstractComponentInvoker {
        List<CompletableFuture> handleCall(Method method, InternalReq internalReq, Object[] args) {
            Res1 res = this.proxyed.call(internalReq.getRequest());
            return this.genCompletableFutures(res);
        }

        List<CompletableFuture> handleApply(Method method, InternalReq internalReq, Object[] args) {
            Res2 res = this.proxyed.apply(internalReq.getRequest(), (Function)args[1]);
            return this.genCompletableFutures(res);
        }

        public ComponentInvoker(Component proxyed) {
            super(proxyed);
        }

        public InvokerParam before(InvokerParam invokerParam) {
            InvokerContext invokerContext = invokerParam.getInvokerContext();
            if (invokerContext == null) {
                invokerParam.setInvokerContext(new InvokerContext());
            }

            invokerParam.getInvokerContext().setSync(true);
            return invokerParam;
        }

        private List<CompletableFuture> genCompletableFutures(Object object) {
            CompletableFuture completableFuture = new CompletableFuture();
            completableFuture.complete(object);
            List<CompletableFuture> completableFutures = new ArrayList();
            completableFutures.add(completableFuture);
            return completableFutures;
        }
    }

    public static class ComponentProxy {
        private Component component;
        private ComponentRuntimeObject componentRuntimeObject;
        private ComponentInvocationHandler.AsyncComponentInvoker asyncComponentInvoker;

        private ComponentProxy() {
        }

        public static ComponentInvocationHandler.ComponentProxy build(Component component) {
            if (component == null) {
                throw new NullPointerException();
            }
            ComponentInvocationHandler.ComponentProxy componentProxy = new ComponentInvocationHandler.ComponentProxy();
            componentProxy.component = component;
            componentProxy.componentRuntimeObject = new ComponentRuntimeObject(component);
            componentProxy.asyncComponentInvoker = new ComponentInvocationHandler.AsyncComponentInvoker(component, componentProxy.componentRuntimeObject);
            return componentProxy;
        }
    }

    private abstract static class AbstractComponentInvoker implements Invoker<InvokerParam> {
        protected Component proxyed;

        public AbstractComponentInvoker(Component proxyed) {
            this.proxyed = proxyed;
        }

        abstract List<CompletableFuture> handleCall(Method method, InternalReq internalReq, Object[] objects);

        abstract List<CompletableFuture> handleApply(Method method, InternalReq internalReq, Object[] objects);

        public InvokerParam before(InvokerParam invokerParam) {
            return invokerParam;
        }

        public Object invoke(InvokerParam invokerParam) {
            this.before(invokerParam);
            Method method = invokerParam.getMethod();
            Object proxy = invokerParam.getProxy();
            Object[] args = invokerParam.getArgs();
            Component.MethodName methodName = ComponentInvocationHandler.methodNameMap.get(method);
            if (methodName == null) {
                try {
                    return method.invoke(this.proxyed, args);
                } catch (IllegalAccessException illegalAccessException) {
                    throw new RuntimeException(illegalAccessException);
                } catch (InvocationTargetException invocationTargetException) {
                    Throwable throwable = invocationTargetException.getTargetException();
                    if(throwable instanceof PontifexRuntimeException) {
                        PontifexRuntimeException pontifexRuntimeException = (PontifexRuntimeException) throwable;
                        throw pontifexRuntimeException;
                    }
                    throw new RuntimeException(invocationTargetException);
                } finally {
                    this.after(invokerParam);
                }
            }
            BaseComponentReq baseComponentReq = (BaseComponentReq)args[0];
            InternalReq internalReq = new InternalReq();
            internalReq.setRequest(baseComponentReq).setSync(baseComponentReq.isSync());
            internalReq.setTaskContext(ThreadLocalUtil.get());
            List completableFutures;
            switch(methodName) {
                case CALL:
                    completableFutures = this.handleCall(method, internalReq, args);
                    break;
                case APPLY:
                    completableFutures = this.handleApply(method, internalReq, args);
                    break;
                default:
                    throw PontifexRuntimeException.exception(ExceptionCode.EXCEPTION_CODE_COMPONENT_METHOD_ERROR);
            }

            this.after(invokerParam);
            Res1 res = new AsyncRes2(completableFutures);
            return res;
        }

        public void after(InvokerParam invokerParam) {
        }
    }

    @Override
    boolean methodHandled(Method method) {
        if(method.getParameterTypes().length == 1 && (method.getReturnType() == CALL.getReturnType() || method.getReturnType() == APPLY.getReturnType())) {
            return true;
        }
        return false;
    }
}
