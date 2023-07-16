package org.wyx.diego.pontifex.pipeline;

import org.wyx.diego.pontifex.exception.ExceptionCode;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.loader.handler.invoke.LogTaskContext;
import org.wyx.diego.pontifex.ListRes;
import org.wyx.diego.pontifex.MapRes;
import org.wyx.diego.pontifex.Response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author diego
 * @time 2015-07-10
 * @description
 */
public class SequencePipeline extends AbstractPipeline implements Iterable<PLTask<?>> {

    private TreeSet<PLTask<?>> posts = new TreeSet<PLTask<?>>(new Comparator<PLTask<?>>() {

        public int compare(PLTask<?> o1, PLTask<?> o2) {
            if(o2.getSort() > o1.getSort()) {
                return -1;


            } else if(o2.getSort() == o1.getSort()) {
                return 0;
            }

            return 1;
        }

    });

    @Override
    public Iterator<PLTask<?>> iterator() {
        return posts.iterator();
    }

    @Override
    public void forEach(Consumer<? super PLTask<?>> action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Spliterator<PLTask<?>> spliterator() {
        return posts.spliterator();
    }

    public Pipeline addTask(PLTask task) {

        posts.add(task);

        return this;

    }

    @Override
    public void run(TaskContext ctx) {

        Iterator<PLTask<?>> iterator = this.iterator();
        Boolean created = new Boolean(false);

        while(iterator.hasNext()) {
            ProxyedTask task = (ProxyedTask)iterator.next();

            try {
                created = analysisParam(created, task, ctx);
                task.run(ctx);
            } catch (Throwable e) {
                throw e;
            }
        }

        LogTaskContext logTaskContext = ctx.getLogTaskContext();
        logger.info("pontifex tasks spend: {}", logTaskContext.toString());
    }

    static Boolean analysisParam(Boolean created, ProxyedTask task, TaskContext ctx) {

        if(created) return created;
        created = true;
        ParameterizedType parameterizedType = getParameterizedType(task);

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Type payloadType = actualTypeArguments[1];
        Class payloadClazz = (Class)payloadType;

        try {
            Payload payload = (Payload)payloadClazz.getConstructor().newInstance();
            ctx.setPayload(payload);
        } catch (Exception e) {
            e.printStackTrace();
            throw PontifexRuntimeException.exception(ExceptionCode.EXCEPTION_CODE_TASK_GENERIC_ERROR);
        }

        Type actualTypeArgument = actualTypeArguments[actualTypeArguments.length - 1];
        if (actualTypeArgument instanceof Class) {
            Class clazz = (Class)actualTypeArgument;

            try {
                Response response = (Response)clazz.getConstructor().newInstance();
                ctx.putPontifexResult(response);
            } catch (Exception e) {
                e.printStackTrace();
                throw PontifexRuntimeException.exception(ExceptionCode.EXCEPTION_CODE_TASK_GENERIC_ERROR);
            }
        } else if (actualTypeArgument instanceof ParameterizedType) {
            ParameterizedType parameterizedTypeT = (ParameterizedType)actualTypeArgument;
            if (parameterizedTypeT.getRawType() == ListRes.class) {
                ListRes<?> listRes = new ListRes();
                ctx.putPontifexResult(listRes);
            } else {
                MapRes<?, ?> mapRes = new MapRes();
                ctx.putPontifexResult(mapRes);
            }
        }

        return created;
    }

    private static ParameterizedType getParameterizedType(ProxyedTask task) {

        Class<?> clazz = task.getPlTask().getClass().getSuperclass();
        ParameterizedType parameterizedType2 = null;
        while(clazz != null && clazz != Object.class) {

            Type type = clazz.getGenericSuperclass();
            if(!(type instanceof ParameterizedType)) {
                clazz = clazz.getSuperclass();
                continue;
            }
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if(actualTypeArguments.length != 3 || !(Payload.class.isAssignableFrom((Class<?>) actualTypeArguments[1]))) {
                clazz = clazz.getSuperclass();
                continue;
            }
            parameterizedType2 = parameterizedType;
            break;

        }
        return parameterizedType2;

    }

    @Override
    public int taskSize() {
        return posts.size();
    }


    @Override
    public int taskIndex(String taskName) {
        if(taskName == null || "".equals(taskName.trim())) throw PontifexRuntimeException.exception(ExceptionCode.EXCEPTION_CODE_TASK);
        int index = 0;
        for(PLTask plTask : posts) {

            if(plTask.name().equals(taskName) ) {
                return index;
            }
            index++;

        }
        return index;
    }
}



