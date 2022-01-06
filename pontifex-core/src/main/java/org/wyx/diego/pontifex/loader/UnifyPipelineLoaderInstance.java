package org.wyx.diego.pontifex.loader;

import org.wyx.diego.pontifex.Loader;
import org.wyx.diego.pontifex.pipeline.PLTask;
import org.wyx.diego.pontifex.pipeline.Task;

/**
 * @author wangyingxin
 * @title: UnifyPipelineLoaderInstance
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/29
 */
public enum UnifyPipelineLoaderInstance implements Loader<Task, Task> {
    INSTANCE(new UnifyPipelineLoader());

    private UnifyPipelineLoader unifyPipelineLoader;
    UnifyPipelineLoaderInstance(UnifyPipelineLoader unifyPipelineLoader) {
        this.unifyPipelineLoader = unifyPipelineLoader;
    }

    @Override
    public Task load(Task task) {
        return unifyPipelineLoader.load(task);
    }

    private static class UnifyPipelineLoader implements Loader<Task, Task> {
        @Override
        public Task load(Task task) {
            return SequencePipelineLoaderInstance.INSTANCE.load(task);
        }
    }
}
