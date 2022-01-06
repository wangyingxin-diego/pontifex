package org.wyx.diego.pontifex.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.Engine;
import org.wyx.diego.pontifex.IPontifexManager;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.engine.StandardPipelineEngineInstance;
import org.wyx.diego.pontifex.exception.ExceptionCode;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.loader.SequencePipelineLoaderInstance;
import org.wyx.diego.pontifex.pipeline.Pipeline;

/**
 * @author wangyingxin
 * @title: DefaultPontifexManagerInstance
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/29
 */
public enum DefaultPontifexManagerInstance implements IPontifexManager {

    INSTANCE(new DefaultPontifexManager());

    private DefaultPontifexManager defaultPontifexManager;

    DefaultPontifexManagerInstance(DefaultPontifexManager defaultPontifexManager) {
        this.defaultPontifexManager = defaultPontifexManager;
    }

    @Override
    public PontifexResponse handler(PontifexRequest pontifexRequest) {
        return defaultPontifexManager.handler(pontifexRequest);
    }

    private static class DefaultPontifexManager implements IPontifexManager {

        private static final Logger logger = LoggerFactory.getLogger(DefaultPontifexManager.class);

        private Engine engine = StandardPipelineEngineInstance.INSTANCE;

        @Override
        public PontifexResponse handler(PontifexRequest pontifexRequest) {

            PontifexResponse pontifexResponse = new PontifexResponse();
            try {
                Pipeline pipeline = SequencePipelineLoaderInstance.INSTANCE.getPipeline(pontifexRequest);
                engine.launch(pipeline, pontifexRequest, pontifexResponse);
                pontifexResponse.setCode(0);
            } catch (Throwable e) {
                if (e instanceof PontifexRuntimeException) {
                    PontifexRuntimeException pontifexRuntimeException = (PontifexRuntimeException)e;
                    pontifexResponse.setCode(pontifexRuntimeException.errorCode);
                    pontifexResponse.setReason(pontifexRuntimeException.userMsg);
                } else {
                    pontifexResponse.setCode(ExceptionCode.EXCEPTION_CODE_BUSINESS_ERROR.getCode());
                    pontifexResponse.setReason(ExceptionCode.EXCEPTION_CODE_BUSINESS_ERROR.getMsg());
                }

                logger.error("pontifex.tast.error", e);
            }

            return pontifexResponse;
        }

    }
}
