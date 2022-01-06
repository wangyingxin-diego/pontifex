package org.wyx.diego.pontifex.pipeline;

import org.wyx.diego.pontifex.ListRes;
import org.wyx.diego.pontifex.MapRes;
import org.wyx.diego.pontifex.Response;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.loader.handler.invoke.LogTaskContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

import static org.wyx.diego.pontifex.exception.ExceptionCode.EXCEPTION_CODE_TASK;

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
        ArrayList timeList = new ArrayList();

        while(iterator.hasNext()) {
            ProxyedTask task = (ProxyedTask)iterator.next();

            try {
                created = analysisParam(created, task, ctx);
                long start = System.currentTimeMillis();
                task.run(ctx);
                long end = System.currentTimeMillis();
                timeList.add(end - start);
            } catch (Throwable e) {
                throw e;
            } finally {
            }
        }

        LogTaskContext logTaskContext = ctx.getLogTaskContext();
        logger.info("pontifex tasks spend: {}", logTaskContext.toString());
    }

    static Boolean analysisParam(Boolean created, ProxyedTask task, TaskContext ctx) {
        if (!created) {
            created = true;
            ParameterizedType parameterizedType = (ParameterizedType)task.getPlTask().getClass().getSuperclass().getGenericSuperclass();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Type payloadType = actualTypeArguments[1];
            Class payloadClazz = (Class)payloadType;

            try {
                Payload payload = (Payload)payloadClazz.getConstructor().newInstance();
                ctx.setPayload(payload);
            } catch (Exception var11) {
                var11.printStackTrace();
            }

            Type actualTypeArgument = actualTypeArguments[actualTypeArguments.length - 1];
            if (actualTypeArgument instanceof Class) {
                Class clazz = (Class)actualTypeArgument;

                try {
                    Response response = (Response)clazz.getConstructor().newInstance();
                    ctx.putPontifexResult(response);
                } catch (Exception var10) {
                    var10.printStackTrace();
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
        }

        return created;
    }


}



