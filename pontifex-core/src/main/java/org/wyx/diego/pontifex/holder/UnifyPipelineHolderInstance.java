package org.wyx.diego.pontifex.holder;

import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.pipeline.Pipeline;
import org.wyx.diego.pontifex.pipeline.Task;

/**
 * @author wangyingxin
 * @title: UnifyPipelineHolderInstance
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/31
 */
public enum UnifyPipelineHolderInstance implements PipelineHolder {

    INSTANCE(new UnifyPipelineHolder());

    private UnifyPipelineHolder unifyPipelineHolder;

    UnifyPipelineHolderInstance(UnifyPipelineHolder unifyPipelineHolder) {
        this.unifyPipelineHolder = unifyPipelineHolder;
    }

    @Override
    public Pipeline obtain(PontifexRequest pontifexRequest) {
        return unifyPipelineHolder.obtain(pontifexRequest);
    }

    @Override
    public Task<?, ?, ?> load(Task<?, ?, ?> task) {
        return unifyPipelineHolder.load(task);
    }

    private static class UnifyPipelineHolder extends AbstractPipelineHolder {

        @Override
        public Pipeline obtain(PontifexRequest pontifexRequest) {
            return SequencePipelineHolderInstance.INSTANCE.obtain(pontifexRequest);
        }

        @Override
        public Task<?, ?, ?> load(Task<?, ?, ?> task) {
            return SequencePipelineHolderInstance.INSTANCE.load(task);
        }

    }

}
