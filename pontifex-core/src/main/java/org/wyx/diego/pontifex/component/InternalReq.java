package org.wyx.diego.pontifex.component;

import org.wyx.diego.pontifex.pipeline.TaskContext;

import java.io.Serializable;

/**
 * @author wangyingxin
 * @title: InternalReq
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/1
 */
public class InternalReq<T extends BaseComponentReq> extends BaseComponentReq implements Serializable {
    private T request;
    private TaskContext taskContext;

    public InternalReq() {
    }

    public T getRequest() {
        return this.request;
    }

    public InternalReq<T> setRequest(T request) {
        this.request = request;
        return this;
    }

    public TaskContext getTaskContext() {
        return this.taskContext;
    }

    public InternalReq<T> setTaskContext(TaskContext taskContext) {
        this.taskContext = taskContext;
        return this;
    }
}
