package org.wyx.diego.pontifex.pipeline;

import org.wyx.diego.pontifex.PontifexRequest;

/**
 * @author wangyingxin
 * @title: PipelineContainer
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/29
 */
public interface PipelineContainer {

    Pipeline getPipeline(PontifexRequest pontifexRequest);

}
