package org.wyx.diego.pontifex.pipeline;

/**
 * @author wangyingxin
 * @title: PipelineName
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/3
 */
public interface PipelineName {
    default String getPipelineName() {
        return this.getClass().getSimpleName();
    }
}
