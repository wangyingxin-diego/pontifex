package org.wyx.diego.pontifex.bytecode;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * @author diego
 * @time 2014-05-20
 * @description
 */
public class ReflectUtils {

    /**
     * void(V).
     */
    public static final char JVM_VOID = 'V';

    /**
     * boolean(Z).
     */
    public static final char JVM_BOOLEAN = 'Z';

    /**
     * byte(B).
     */
    public static final char JVM_BYTE = 'B';

    /**
     * char(C).
     */
    public static final char JVM_CHAR = 'C';

    /**
     * double(D).
     */
    public static final char JVM_DOUBLE = 'D';

    /**
     * float(F).
     */
    public static final char JVM_FLOAT = 'F';

    /**
     * int(I).
     */
    public static final char JVM_INT = 'I';

    /**
     * long(J).
     */
    public static final char JVM_LONG = 'J';

    /**
     * short(S).
     */
    public static final char JVM_SHORT = 'S';

    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    public static final String JAVA_IDENT_REGEX = "(?:[_$a-zA-Z][_$a-zA-Z0-9]*)";

    public static final String JAVA_NAME_REGEX = "(?:" + JAVA_IDENT_REGEX + "(?:\\." + JAVA_IDENT_REGEX + ")*)";

    public static final String CLASS_DESC = "(?:L" + JAVA_IDENT_REGEX + "(?:\\/" + JAVA_IDENT_REGEX + ")*;)";

    public static final String ARRAY_DESC = "(?:\\[+(?:(?:[VZBCDFIJS])|" + CLASS_DESC + "))";

    public static final String DESC_REGEX = "(?:(?:[VZBCDFIJS])|" + CLASS_DESC + "|" + ARRAY_DESC + ")";

    public static final Pattern DESC_PATTERN = Pattern.compile(DESC_REGEX);

    public static final String METHOD_DESC_REGEX = "(?:(" + JAVA_IDENT_REGEX + ")?\\((" + DESC_REGEX + "*)\\)("
            + DESC_REGEX + ")?)";

    public static final Pattern METHOD_DESC_PATTERN = Pattern.compile(METHOD_DESC_REGEX);

    public static final Pattern GETTER_METHOD_DESC_PATTERN = Pattern.compile("get([A-Z][_a-zA-Z0-9]*)\\(\\)("
            + DESC_REGEX + ")");

    public static final Pattern SETTER_METHOD_DESC_PATTERN = Pattern.compile("set([A-Z][_a-zA-Z0-9]*)\\((" + DESC_REGEX
            + ")\\)V");

    public static final Pattern IS_HAS_CAN_METHOD_DESC_PATTERN = Pattern
            .compile("(?:is|has|can)([A-Z][_a-zA-Z0-9]*)\\(\\)Z");

    private static final ConcurrentMap<String, Class<?>> DESC_CLASS_CACHE = new ConcurrentHashMap<String, Class<?>>();

    private static final ConcurrentMap<String, Class<?>> NAME_CLASS_CACHE = new ConcurrentHashMap<String, Class<?>>();

    private static final ConcurrentMap<String, Method> Signature_METHODS_CACHE = new ConcurrentHashMap<String, Method>();

    static private Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>();

    static {
        primitiveDefaults.put(int.class, 0);
        primitiveDefaults.put(long.class, 0L);
        primitiveDefaults.put(byte.class, (byte) 0);
        primitiveDefaults.put(char.class, (char) 0);
        primitiveDefaults.put(short.class, (short) 0);
        primitiveDefaults.put(float.class, (float) 0);
        primitiveDefaults.put(double.class, (double) 0);
        primitiveDefaults.put(boolean.class, false);
        primitiveDefaults.put(void.class, null);
    }

    public static String getDesc(Class<?> c) {
        StringBuilder ret = new StringBuilder();

        while (c.isArray()) {
            ret.append('[');
            c = c.getComponentType();
        }

        if (c.isPrimitive()) {
            String t = c.getName();
            if ("void".equals(t))
                ret.append(JVM_VOID);
            else if ("boolean".equals(t))
                ret.append(JVM_BOOLEAN);
            else if ("byte".equals(t))
                ret.append(JVM_BYTE);
            else if ("char".equals(t))
                ret.append(JVM_CHAR);
            else if ("double".equals(t))
                ret.append(JVM_DOUBLE);
            else if ("float".equals(t))
                ret.append(JVM_FLOAT);
            else if ("int".equals(t))
                ret.append(JVM_INT);
            else if ("long".equals(t))
                ret.append(JVM_LONG);
            else if ("short".equals(t))
                ret.append(JVM_SHORT);
        } else {
            ret.append('L');
            ret.append(c.getName().replace('.', '/'));
            ret.append(';');
        }
        return ret.toString();
    }

    public static String getDesc(final Class<?>[] cs) {
        if (cs.length == 0)
            return "";

        StringBuilder sb = new StringBuilder(64);
        for (Class<?> c : cs)
            sb.append(getDesc(c));
        return sb.toString();
    }

    public static String getDesc(final Method m) {
        StringBuilder ret = new StringBuilder(m.getName()).append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
            ret.append(getDesc(parameterTypes[i]));
        ret.append(')').append(getDesc(m.getReturnType()));
        return ret.toString();
    }

    public static String getDesc(final Constructor<?> c) {
        StringBuilder ret = new StringBuilder("(");
        Class<?>[] parameterTypes = c.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
            ret.append(getDesc(parameterTypes[i]));
        ret.append(')').append('V');
        return ret.toString();
    }

    public static String getDescWithoutMethodName(Method m) {
        StringBuilder ret = new StringBuilder();
        ret.append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
            ret.append(getDesc(parameterTypes[i]));
        ret.append(')').append(getDesc(m.getReturnType()));
        return ret.toString();
    }

    public static String getDesc(final CtClass c) throws NotFoundException {
        StringBuilder ret = new StringBuilder();
        if (c.isArray()) {
            ret.append('[');
            ret.append(getDesc(c.getComponentType()));
        } else if (c.isPrimitive()) {
            String t = c.getName();
            if ("void".equals(t))
                ret.append(JVM_VOID);
            else if ("boolean".equals(t))
                ret.append(JVM_BOOLEAN);
            else if ("byte".equals(t))
                ret.append(JVM_BYTE);
            else if ("char".equals(t))
                ret.append(JVM_CHAR);
            else if ("double".equals(t))
                ret.append(JVM_DOUBLE);
            else if ("float".equals(t))
                ret.append(JVM_FLOAT);
            else if ("int".equals(t))
                ret.append(JVM_INT);
            else if ("long".equals(t))
                ret.append(JVM_LONG);
            else if ("short".equals(t))
                ret.append(JVM_SHORT);
        } else {
            ret.append('L');
            ret.append(c.getName().replace('.', '/'));
            ret.append(';');
        }
        return ret.toString();
    }

    public static String getDesc(final CtMethod m) throws NotFoundException {
        StringBuilder ret = new StringBuilder(m.getName()).append('(');
        CtClass[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
            ret.append(getDesc(parameterTypes[i]));
        ret.append(')').append(getDesc(m.getReturnType()));
        return ret.toString();
    }

    public static String getDesc(final CtConstructor c) throws NotFoundException {
        StringBuilder ret = new StringBuilder("(");
        CtClass[] parameterTypes = c.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
            ret.append(getDesc(parameterTypes[i]));
        ret.append(')').append('V');
        return ret.toString();
    }

    public static String getName(Class<?> c) {
        if (c.isArray()) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append("[]");
                c = c.getComponentType();
            } while (c.isArray());

            return c.getName() + sb.toString();
        }
        return c.getName();
    }

    public static String getName(final Method m) {
        StringBuilder ret = new StringBuilder();
        ret.append(getName(m.getReturnType())).append(' ');
        ret.append(m.getName()).append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0)
                ret.append(',');
            ret.append(getName(parameterTypes[i]));
        }
        ret.append(')');
        return ret.toString();
    }


    public static void getAllInterfaces(Class<?> clazz, List<Class<?>> superClazzList) {
        if (clazz == null) {
            return;
        }
        if (!superClazzList.contains(clazz)) {
            if(clazz.isInterface()) {
                superClazzList.add(clazz);
            }
            Class<?> superclass = clazz.getSuperclass();
            getAllInterfaces(superclass, superClazzList);
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> interfaceCls : interfaces) {
                getAllInterfaces(interfaceCls, superClazzList);
            }
        }
    }

    private ReflectUtils() {
    }

}
