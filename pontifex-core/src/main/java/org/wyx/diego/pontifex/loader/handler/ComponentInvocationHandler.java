package org.wyx.diego.pontifex.loader.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.Component;
import org.wyx.diego.pontifex.component.*;
import org.wyx.diego.pontifex.loader.handler.invoke.Invoker;
import org.wyx.diego.pontifex.loader.handler.invoke.InvokerContext;
import org.wyx.diego.pontifex.loader.handler.invoke.InvokerParam;
import org.wyx.diego.pontifex.loader.runtime.Async;
import org.wyx.diego.pontifex.loader.runtime.ComponentRuntimeObject;
import org.wyx.diego.pontifex.loader.runtime.RuntimeObject;
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
    private static final Logger logger = LoggerFactory.getLogger(ComponentInvocationHandler.class);
    private static final Map<Method, Component.MethodName> methodNameMap = new ConcurrentHashMap();

    public ComponentInvocationHandler(ComponentInvocationHandler.ComponentProxy componentProxy) {
        super(componentProxy.componentRuntimeObject, componentProxy.asyncComponentInvoker, componentProxy.component);
    }

    static {
        Method[] methods = Component.class.getDeclaredMethods();
        Method[] var2 = methods;
        int var3 = methods.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Method method = var2[var4];
            String methodName = method.getName();
            Component.MethodName name = Component.MethodName.getByMethodName(methodName);
            if (name != null) {
                methodNameMap.put(method, name);
            }
        }

    }

    private static final class AsyncComponentInvoker extends ComponentInvocationHandler.ComponentInvoker {
        private final ExecutorService executorService;
        private final ComponentRuntimeObject componentRuntimeObject;
        private final boolean defaultAsync;

        public AsyncComponentInvoker(Component proxyed, ComponentRuntimeObject componentRuntimeObject) {
            super(proxyed);
            String name = proxyed.getClass().getSimpleName();
            this.componentRuntimeObject = componentRuntimeObject;
            Async async = this.componentRuntimeObject.getAsync();
            short concurrency = async.getConcurrency();
            this.executorService = Executors.newFixedThreadPool(concurrency, new ComponentInvocationHandler.AsyncComponentInvoker.ComponentThreadFactory(name));
            this.defaultAsync = this.componentRuntimeObject.getAsync().isOpen();
        }

        public InvokerParam before(InvokerParam invokerParam) {
            return invokerParam;
        }

        public void after(InvokerParam o) {
        }

        List<CompletableFuture> handleCall(Method method, InternalReq internalReq, Object[] args) {
            if (this.defaultAsync && !internalReq.isSync()) {
                CompletableFuture completableFuture = CompletableFuture.supplyAsync(() -> {
                    ThreadLocalUtil.setTaskContext(internalReq.getTaskContext());

                    Res1 object;
                    try {
                        object = this.proxyed.call(internalReq.getRequest());
                    } catch (Exception var7) {
                        throw var7;
                    } finally {
                        ThreadLocalUtil.remove();
                    }

                    return object;
                }, this.executorService);
                List<CompletableFuture> completableFutures = new ArrayList();
                completableFutures.add(completableFuture);
                return completableFutures;
            } else {
                return super.handleCall(method, internalReq, args);
            }
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
            } else {
                ComponentInvocationHandler.ComponentProxy componentProxy = new ComponentInvocationHandler.ComponentProxy();
                componentProxy.component = component;
                componentProxy.componentRuntimeObject = new ComponentRuntimeObject(component);
                componentProxy.asyncComponentInvoker = new ComponentInvocationHandler.AsyncComponentInvoker(component, componentProxy.componentRuntimeObject);
                return componentProxy;
            }
        }
    }

    private abstract static class AbstractComponentInvoker implements Invoker<InvokerParam> {
        protected Component proxyed;

        public AbstractComponentInvoker(Component proxyed) {
            this.proxyed = proxyed;
        }

        abstract List<CompletableFuture> handleCall(Method var1, InternalReq var2, Object[] var3);

        abstract List<CompletableFuture> handleApply(Method var1, InternalReq var2, Object[] var3);

        public InvokerParam before(InvokerParam invokerParam) {
            return invokerParam;
        }

        public Object invoke(InvokerParam invokerParam) {
            this.before(invokerParam);
            Method method = invokerParam.getMethod();
            Object proxy = invokerParam.getProxy();
            Object[] args = invokerParam.getArgs();
            Component.MethodName methodName = (Component.MethodName)ComponentInvocationHandler.methodNameMap.get(method);
            if (methodName == null) {
                try {
                    return method.invoke(this.proxyed, args);
                } catch (IllegalAccessException var10) {
                    throw new RuntimeException(var10);
                } catch (InvocationTargetException var11) {
                    throw new RuntimeException(var11);
                }
            } else {
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
                        throw new RuntimeException();
                }

                this.after(invokerParam);
                Res1 res = new AsyncRes2(completableFutures);
                return res;
            }
        }

        public void after(InvokerParam o) {
        }
    }
}
