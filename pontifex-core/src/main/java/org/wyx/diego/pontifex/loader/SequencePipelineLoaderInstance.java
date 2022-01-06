package org.wyx.diego.pontifex.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.annotation.RuntimeMeta;
import org.wyx.diego.pontifex.annotation.TaskMeta;
import org.wyx.diego.pontifex.holder.UnifyPipelineHolderInstance;
import org.wyx.diego.pontifex.pipeline.PLTask;
import org.wyx.diego.pontifex.pipeline.Pipeline;
import org.wyx.diego.pontifex.pipeline.Task;

/**
 * @author wangyingxin
 * @title: SequencePipelineLoaderInstance
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/29
 */
public enum SequencePipelineLoaderInstance implements PipelineLoader<Task, Task> {

    INSTANCE(new SequencePipelineLoader());

    private SequencePipelineLoader sequencePipelineLoader;

    SequencePipelineLoaderInstance(SequencePipelineLoader sequencePipelineLoader) {
        this.sequencePipelineLoader = sequencePipelineLoader;
    }


    @Override
    public Task load(Task task) {
        return sequencePipelineLoader.load(task);
    }

    @Override
    public Pipeline getPipeline(PontifexRequest pontifexRequest) {
        return sequencePipelineLoader.getPipeline(pontifexRequest);
    }

    private static class SequencePipelineLoader implements PipelineLoader<Task, Task> {

        private static final Logger logger = LoggerFactory.getLogger(SequencePipelineLoader.class);

        public Pipeline getPipeline(PontifexRequest pontifexRequest) {
            return UnifyPipelineHolderInstance.INSTANCE.obtain(pontifexRequest);
        }

        @Override
        public Task load(Task task) {

            UnifyPipelineHolderInstance.INSTANCE.load(task);
            return task;

        }
    }
    private static class TaskParam {

        private PLTask task;

        private TaskMeta taskMeta;

        private RuntimeMeta defaultRuntimeMeta;

        public PLTask getTask() {
            return task;
        }

        public TaskParam setTask(PLTask task) {
            this.task = task;
            return this;
        }

        public TaskMeta getTaskMeta() {
            return taskMeta;
        }

        public TaskParam setTaskMeta(TaskMeta taskMeta) {
            this.taskMeta = taskMeta;
            return this;
        }

        public RuntimeMeta getDefaultRuntimeMeta() {
            return defaultRuntimeMeta;
        }

        public TaskParam setDefaultRuntimeMeta(RuntimeMeta defaultRuntimeMeta) {
            this.defaultRuntimeMeta = defaultRuntimeMeta;
            return this;
        }
    }
}
