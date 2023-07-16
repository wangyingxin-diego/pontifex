package org.wyx.diego.pontifex.demo.business.user;

import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.annotation.Cache;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.annotation.PipelineMeta;
import org.wyx.diego.pontifex.cache.DefaultPipelineGetKey;
import org.wyx.diego.pontifex.cache.GetKey;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.flow.annotation.Flow;
import org.wyx.diego.pontifex.flow.annotation.FlowDeGrade;
import org.wyx.diego.pontifex.manager.DefaultPontifexManagerInstance;
import org.wyx.diego.pontifex.pipeline.AbstractPipelineInterface;
import org.wyx.diego.pontifex.spring.annotation.PipelineMateSpring;

import static org.wyx.diego.pontifex.demo.business.user.UserTaskConstants.USER_ADD_PIPELINE;

@PipelineMateSpring(pipelineMeta = @PipelineMeta(name = USER_ADD_PIPELINE, cache = @Cache(isOpen = true), flowDeGrade = @FlowDeGrade(flow = @Flow(count = 1))))
public class UserPipeline extends AbstractPipelineInterface {

    @Override
    public PontifexResponse call(PontifexRequest pontifexRequest) {
        PontifexResponse pontifexResponse = DefaultPontifexManagerInstance.INSTANCE.handler(pontifexRequest);
        return pontifexResponse;
    }

    @Override
    public GetKey<PontifexRequest> getKey() {
        return DefaultPipelineGetKey.DEFAULT_GET_KEY;
    }

    @Override
    public PontifexResponse fallback(PontifexRequest pontifexRequest) {
        throw new PontifexRuntimeException(113333, "降级了");
    }
}
