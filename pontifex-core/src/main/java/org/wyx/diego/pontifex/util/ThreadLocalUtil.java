package org.wyx.diego.pontifex.util;


import org.wyx.diego.pontifex.pipeline.TaskContext;

/**
 * @author diego
 * @time 2015-10-25
 * @description
 */
public class ThreadLocalUtil {

    private static final ThreadLocal<TaskContext> tltc = new ThreadLocal<>();

    public static void setTaskContext(TaskContext taskContext) {

        tltc.set(taskContext);

    }

    public static TaskContext get() {
        return tltc.get();
    }

    public static void remove() {
        tltc.remove();
    }

}
