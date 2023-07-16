package org.wyx.diego.pontifex.engine;

import org.wyx.diego.pontifex.Engine;
import org.wyx.diego.pontifex.loader.handler.invoke.LogTaskContext;
import org.wyx.diego.pontifex.pipeline.Pipeline;
import org.wyx.diego.pontifex.pipeline.TaskContext;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.util.ThreadLocalUtil;

/**
 * @author wangyingxin
 * @title: StandardPipelineEngineInstance
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/29
 */
public enum StandardPipelineEngineInstance implements Engine {

    INSTANCE(new StandardPipelineEngine());

    private StandardPipelineEngine standardPipelineEngine;
    StandardPipelineEngineInstance(StandardPipelineEngine standardPipelineEngine) {
        this.standardPipelineEngine = standardPipelineEngine;
    }

    @Override
    public void launch(Pipeline pipeline, PontifexRequest pontifexRequest, PontifexResponse pontifexResponse) {
        this.standardPipelineEngine.launch(pipeline, pontifexRequest, pontifexResponse);
    }

    private static class StandardPipelineEngine implements Engine {

        @Override
        public void launch(Pipeline pipeline, PontifexRequest pontifexRequest, PontifexResponse pontifexResponse) {

            TaskContext context = new TaskContext();
            context.setPontifexRequest(pontifexRequest);
            context.setPontifexResponse(pontifexResponse);
            context.setLogTaskContext(LogTaskContext.newInstance());
            try {
                ThreadLocalUtil.setTaskContext(context);
                context.run(pipeline);
            } catch (Throwable e) {
                throw e;
            } finally {
                ThreadLocalUtil.remove();
            }

        }
    }

}
