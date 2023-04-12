package org.wyx.diego.pontifex.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.*;
import org.wyx.diego.pontifex.engine.StandardPipelineEngineInstance;
import org.wyx.diego.pontifex.exception.ExceptionCode;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.filter.PontifexMainFilter;
import org.wyx.diego.pontifex.filter.StandardPipelineFilerChain;
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

        private PontifexMainFilter pontifexMainFilter = new PontifexMainFilter(engine);

        @Override
        public PontifexResponse handler(PontifexRequest pontifexRequest) {

            PontifexResponse pontifexResponse = new PontifexResponse();
            try {
                Pipeline pipeline = SequencePipelineLoaderInstance.INSTANCE.getPipeline(pontifexRequest);
                pontifexMainFilter.doFilter(pontifexRequest, pontifexResponse, new StandardPipelineFilerChain());
                pontifexResponse.getMeta().setCode(0).setMessage("success");
            } catch (Throwable e) {
                if (e instanceof PontifexRuntimeException) {
                    PontifexRuntimeException pontifexRuntimeException = (PontifexRuntimeException)e;
                    pontifexResponse.getMeta().setCode(pontifexRuntimeException.errorCode);
                    pontifexResponse.getMeta().setMessage(pontifexRuntimeException.userMsg);
                } else {
                    pontifexResponse.getMeta().setCode(ExceptionCode.EXCEPTION_CODE_BUSINESS_ERROR.getCode());
                    pontifexResponse.getMeta().setMessage(ExceptionCode.EXCEPTION_CODE_BUSINESS_ERROR.getMsg());
                }
                pontifexResponse.setResult(DefaultFailResponse.DEFAULT_FAIL_RESPONSE.getDefaultFailResponseVal());

                logger.error("pontifex.task.error, code={}, reason={}", pontifexResponse.getMeta().getCode(), pontifexResponse.getMeta().getMessage(), e);
            }

            return pontifexResponse;
        }

    }
}
