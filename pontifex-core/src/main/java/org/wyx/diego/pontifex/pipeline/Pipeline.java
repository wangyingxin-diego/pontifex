package org.wyx.diego.pontifex.pipeline;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

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

    int taskSize();

    int taskIndex(String taskName);

    Iterator<PLTask<?>> iterator();

}
