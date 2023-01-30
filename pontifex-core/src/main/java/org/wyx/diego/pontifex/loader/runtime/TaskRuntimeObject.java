package org.wyx.diego.pontifex.loader.runtime;

import org.wyx.diego.pontifex.ModuleType;
import org.wyx.diego.pontifex.annotation.TaskMeta;
import org.wyx.diego.pontifex.exception.ExceptionCode;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.log.TaskLogLevel;
import org.wyx.diego.pontifex.pipeline.PLTask;

/**
 * @author wangyingxin
 * @title: TaskRuntimeObject
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/31
 */
public class TaskRuntimeObject extends RuntimeObject {

    private final String pipelineName;
    private final String name;
    private final TaskLogLevel taskLogLevel;
    private final int sort;

    private final int innerSort;

    public TaskRuntimeObject(TaskMeta taskMeta, PLTask plTask, int innerSort) {
        super(taskMeta.runtime(), ModuleType.MODULE_TYPE_TASK);
        String name = taskMeta.name();
        if (name == null || "".equals(name.trim())) {
            name = plTask.getClass().getSimpleName();
        }

        this.name = name;
        String pipelineName = taskMeta.pipelineName();
        if (pipelineName != null && !"".equals(pipelineName.trim())) {
            this.pipelineName = taskMeta.pipelineName().trim();
            this.taskLogLevel = taskMeta.level();
            this.sort = taskMeta.sort();
            this.innerSort = innerSort;
        } else {
            throw new PontifexRuntimeException(ExceptionCode.EXCEPTION_CODE_PL_NAME_NULL);
        }
    }

    public String getPipelineName() {
        return this.pipelineName;
    }

    public String getName() {
        return this.name;
    }

    public TaskLogLevel getTaskLogLevel() {
        return this.taskLogLevel;
    }

    public int getSort() {
        return this.sort;
    }

    public int getInnerSort() {
        return innerSort;
    }
}
