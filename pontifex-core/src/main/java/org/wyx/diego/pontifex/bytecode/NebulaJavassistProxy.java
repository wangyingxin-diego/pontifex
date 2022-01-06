package org.wyx.diego.pontifex.bytecode;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author diego
 * @time 2014-05-20
 * @description
 */
public abstract class NebulaJavassistProxy {

    private static final AtomicLong PROXY_CLASS_COUNTER = new AtomicLong(0);

    private static final String PACKAGE_NAME = NebulaJavassistProxy.class.getPackage().getName();

    public static final InvocationHandler RETURN_NULL_INVOKER = (Object proxy, Method method, Object[] args) -> null;

    public static final InvocationHandler THROW_UNSUPPORTED_INVOKER = (Object proxy, Method method, Object[] args) -> {
        throw new UnsupportedOperationException("Method [" + ReflectUtils.getName(method) + "] unimplemented.");
    };

    private static final Map<ClassLoader, Map<String, Object>> PROXY_CACHE_MAP = new WeakHashMap<>();

    private static final Object PENDING_GENERATION_MARKER_OBJECT = new Object();

    private static final Map<String, Integer> OBJECT_METHODS = new HashMap();
    static {
        for(Method method : Object.class.getMethods()) {
            OBJECT_METHODS.put(method.getName(), 1);
        }
    }

    public static NebulaJavassistProxy getProxy(Class<?>... interfaces) {

        return getProxy(NebulaJavassistProxy.class, interfaces);

    }

    public static NebulaJavassistProxy getProxy(Class<?> surperClass) {

        Class<?>[] classes = new Class<?>[0];
        return getProxy(surperClass, classes);
    }

    public static NebulaJavassistProxy getProxy(Class<?> surperClass, Class<?>... interfaces) {

        if ((interfaces.length > 65535 || interfaces.length < 1) && surperClass == null)
            throw new IllegalArgumentException("0 <= interface < 65535");
        if ((interfaces.length > 65535 || interfaces.length < 0) && surperClass != null)
            throw new IllegalArgumentException("0 <= interface < 65535");

        ClassLoader cl;
        ClassLoader appClassLoader;
        ProtectionDomain domain;
        if(surperClass != null) {
            cl = surperClass.getClassLoader();
            appClassLoader = surperClass.getClassLoader();
            domain = surperClass.getProtectionDomain();
        } else {
            cl = interfaces[0].getClassLoader();
            appClassLoader = interfaces[0].getClassLoader();
            domain = interfaces[0].getProtectionDomain();
        }

        String key = genKey(cl, surperClass, interfaces);
        Map<String, Object> cache;
        synchronized (PROXY_CACHE_MAP) {
            cache = PROXY_CACHE_MAP.get(cl);
            if (cache == null) {
                cache = new HashMap<>();
                PROXY_CACHE_MAP.put(cl, cache);
            }
        }

        NebulaJavassistProxy proxy = null;
        synchronized (cache) {
            do {
                Object value = cache.get(key);
                if (value instanceof Reference<?>) {
                    proxy = (NebulaJavassistProxy) ((Reference<?>) value).get();
                    if (proxy != null)
                        return proxy;
                }

                if (value == PENDING_GENERATION_MARKER_OBJECT) {
                    try {
                        cache.wait();
                    } catch (InterruptedException e) {
                    }
                } else {
                    cache.put(key, PENDING_GENERATION_MARKER_OBJECT);
                    break;
                }
            } while (true);
        }

        long id = PROXY_CLASS_COUNTER.getAndIncrement();
        String pkg = null;
        NebulaJavassistClassGenerator ccp = null, ccm = null;
        try {
            ccp = NebulaJavassistClassGenerator.newInstance(cl);

            Set<String> worked = new HashSet<>();
            List<Method> methods = new ArrayList<>();

            handleSuperClass(surperClass, ccp, worked, methods);
            pkg = handleInterfaces(ccp, worked, methods, interfaces);

            if (pkg == null)
                pkg = PACKAGE_NAME;

            String pcn = pkg + ".proxy" + id;
            ccp.setClassName(pcn);
            ccp.addField("public static java.lang.reflect.Method[] methods;");
            ccp.addField("private " + InvocationHandler.class.getName() + " handler;");
            ccp.addConstructor(Modifier.PUBLIC, new Class<?>[] { InvocationHandler.class }, new Class<?>[0],
                    "handler=$1;");
            ccp.addDefaultConstructor();
            ccp.setSuperClass(surperClass);
            Class<?> clazz = ccp.toClass(appClassLoader, domain);
            clazz.getField("methods").set(null, methods.toArray(new Method[methods.size()]));

            String fcn = NebulaJavassistProxy.class.getName() + id;
            ccm = NebulaJavassistClassGenerator.newInstance(cl);
            ccm.setClassName(fcn);
            ccm.addDefaultConstructor();
            ccm.setSuperClass(NebulaJavassistProxy.class);
            ccm.addMethod("public Object newInstance(" + InvocationHandler.class.getName() + " h){ return new " + pcn
                    + "($1); }");
            Class<?> pc = ccm.toClass(appClassLoader, domain);
            proxy =  (NebulaJavassistProxy)pc.newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (ccp != null)
                ccp.release();
            if (ccm != null)
                ccm.release();
            synchronized (cache) {
                if (proxy == null)
                    cache.remove(key);
                else
                    cache.put(key, new WeakReference<>(proxy));
                cache.notifyAll();
            }
        }
        return proxy;
    }

    public Object newInstance() {
        return newInstance(THROW_UNSUPPORTED_INVOKER);
    }

    abstract public Object newInstance(InvocationHandler handler);

    protected NebulaJavassistProxy() {
    }

    private static String genKey(ClassLoader cl, Class<?> surperClass, Class<?>... interfaces) {

        StringBuilder sb = new StringBuilder();
        if(surperClass != null) {
            sb.append(surperClass.getName()).append(";");
        }
        for (int i = 0; i < interfaces.length; i++) {
            String itf = interfaces[i].getName();
            if (!interfaces[i].isInterface())
                throw new RuntimeException(itf + " is not a interface.");

            Class<?> tmp = null;
            try {
                tmp = Class.forName(itf, false, cl);
            } catch (ClassNotFoundException e) {
            }

            if (tmp != interfaces[i])
                throw new IllegalArgumentException(interfaces[i] + " is not visible from class loader");

            sb.append(itf).append(';');
        }

        String key = sb.toString();

        return key;

    }

    private static void handleSuperClass(Class<?> surperClass, NebulaJavassistClassGenerator ccp, Set<String> worked, List<Method> methods) {

        if(surperClass != null) {

            for (Method method : surperClass.getMethods()) {
                String desc = ReflectUtils.getDesc(method);
                if (worked.contains(desc))
                    continue;
                int mo = method.getModifiers();
                if(Modifier.isFinal(mo) || Modifier.isNative(mo) || Modifier.isStatic(mo)) {
                    continue;
                }
                if(OBJECT_METHODS.get(method.getName()) != null) {
                    continue;
                }
                worked.add(desc);

                genMethod(method, ccp, methods);
            }

        }

    }

    private static String handleInterfaces(NebulaJavassistClassGenerator ccp, Set<String> worked, List<Method> methods, Class<?>... interfaces) {

        String pkg = null;
        for (int i = 0; i < interfaces.length; i++) {
//            if (interfaces[i] == Comparable.class) continue;
            if (!Modifier.isPublic(interfaces[i].getModifiers())) {
                String npkg = interfaces[i].getPackage().getName();
                if (pkg == null) {
                    pkg = npkg;
                } else {
                    if (!pkg.equals(npkg))
                        throw new IllegalArgumentException("non-public interfaces from different packages");
                }
            }
            ccp.addInterface(interfaces[i]);

            for (Method method : interfaces[i].getMethods()) {
                String desc = ReflectUtils.getDesc(method);
                if (worked.contains(desc))
                    continue;
                worked.add(desc);
                genMethod(method, ccp, methods);
            }
        }

        return pkg;

    }

    private static void genMethod(Method method, NebulaJavassistClassGenerator ccp, List<Method> methods) {

        int ix = methods.size();
        Class<?> rt = method.getReturnType();
        Class<?>[] pts = method.getParameterTypes();

        StringBuilder code = new StringBuilder("Object[] args = new Object[").append(pts.length).append(
                "];");
        for (int j = 0; j < pts.length; j++)
            code.append(" args[").append(j).append("] = ($w)$").append(j + 1).append(";");
        code.append(" Object ret = handler.invoke(this, methods[" + ix + "], args);");
        if (!Void.TYPE.equals(rt))
            code.append(" return ").append(asArgument(rt, "ret")).append(";");

        methods.add(method);
        ccp.addMethod(method.getName(), method.getModifiers(), rt, pts, method.getExceptionTypes(),
                code.toString());

    }

    private static String asArgument(Class<?> cl, String name) {
        if (cl.isPrimitive()) {
            if (Boolean.TYPE == cl)
                return name + "==null?false:((Boolean)" + name + ").booleanValue()";
            if (Byte.TYPE == cl)
                return name + "==null?(byte)0:((Byte)" + name + ").byteValue()";
            if (Character.TYPE == cl)
                return name + "==null?(char)0:((Character)" + name + ").charValue()";
            if (Double.TYPE == cl)
                return name + "==null?(double)0:((Double)" + name + ").doubleValue()";
            if (Float.TYPE == cl)
                return name + "==null?(float)0:((Float)" + name + ").floatValue()";
            if (Integer.TYPE == cl)
                return name + "==null?(int)0:((Integer)" + name + ").intValue()";
            if (Long.TYPE == cl)
                return name + "==null?(long)0:((Long)" + name + ").longValue()";
            if (Short.TYPE == cl)
                return name + "==null?(short)0:((Short)" + name + ").shortValue()";
            throw new RuntimeException(name + " is unknown primitive type.");
        }
        return "(" + ReflectUtils.getName(cl) + ")" + name;
    }

}
