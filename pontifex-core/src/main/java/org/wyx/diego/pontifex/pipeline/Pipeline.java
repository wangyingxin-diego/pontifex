package org.wyx.diego.pontifex.pipeline;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.loader.handler.invoke.LogTaskContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.wyx.diego.pontifex.exception.ExceptionCode.EXCEPTION_CODE_TASK;

/**
 * @author diego
 * @time 2015-07-10
 * @description
 */
public interface Pipeline {

    Logger logger = LoggerFactory.getLogger(Pipeline.class);

    static void run(TaskContext ctx, Pipeline pipeline) {

        pipeline.run(ctx);

    }


    void run(TaskContext ctx);
    Pipeline addTask(PLTask<?> task);

}
