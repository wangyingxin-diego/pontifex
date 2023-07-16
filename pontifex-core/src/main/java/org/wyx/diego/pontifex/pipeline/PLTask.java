package org.wyx.diego.pontifex.pipeline;


import org.wyx.diego.pontifex.exception.BusinessException;
import org.wyx.diego.pontifex.exception.ExceptionLevel;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.exception.ExceptionType;

/**
 * @author diego
 * @time 2015-07-10
 * @description
 */
public abstract class PLTask<T extends PLContext> implements Comparable<PLTask<?>> {

    private int sort;

    private int innerSort;

    private TaskType TaskType;

    private String name;

//    public PLTask(int sort, org.wyx.diego.pontifex.pipeline.TaskType taskType, String name) {
//        this.sort = sort;
//        TaskType = taskType;
//        this.name = name;
//    }

    public abstract void run(T ctx);

    public final String name() {
        return name;
    };

    public abstract int getType();

    private static void throwException(int errorCode, String userMsg) {
        throw new PontifexRuntimeException(errorCode, userMsg, userMsg, ExceptionType.TASK_EXCEPTION, ExceptionLevel.EXCEPTION_MUST);
    }

    private static void throwException(int errorCode, String debugMsg, String userMsg) {
        throw new PontifexRuntimeException(errorCode, debugMsg, userMsg, ExceptionType.TASK_EXCEPTION, ExceptionLevel.EXCEPTION_MUST);
    }

    private static void throwException(int errorCode, String debugMsg, String userMsg, ExceptionLevel exceptionLevel) {
        throw new PontifexRuntimeException(errorCode, debugMsg, userMsg, ExceptionType.TASK_EXCEPTION, exceptionLevel);
    }

    protected static void throwException(BusinessException businessException) {
        throw PontifexRuntimeException.exception(businessException);
    }

    protected static void throwDefaultValueException() {
        throw PontifexRuntimeException.DEFAULT_VALUE_EXCEPTION;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public org.wyx.diego.pontifex.pipeline.TaskType getTaskType() {
        return TaskType;
    }

    public void setTaskType(org.wyx.diego.pontifex.pipeline.TaskType taskType) {
        TaskType = taskType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInnerSort() {
        return innerSort;
    }

    public void setInnerSort(int innerSort) {
        this.innerSort = innerSort;
    }

    public int compareTo(PLTask o) {
        if (this.getSort() > o.getSort()) {
            return 1;
        } else if (this.getSort() == o.getSort()) {
            return 0;
        }

        return -1;
    }

    static enum MethodName {
        RUN("run");

        private String methodName;

        private MethodName(String methodName) {
            this.methodName = methodName;
        }

        public static PLTask.MethodName getByMethodName(String methodName) {

            for(MethodName methodName1 : values()) {
                if (methodName1.methodName.equals(methodName)) {
                    return methodName1;
                }
            }

            return null;
        }
    }
}
