package org.wyx.diego.pontifex.holder;

import org.wyx.diego.pontifex.pipeline.Pipeline;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangyingxin
 * @title: AbstractPipelineHolder
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/30
 */
abstract class AbstractPipelineHolder implements PipelineHolder {

    protected static final ConcurrentHashMap<String, Pipeline> pipelines = new ConcurrentHashMap();

}
