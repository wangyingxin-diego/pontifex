package org.wyx.diego.pontifex.filter;


import org.wyx.diego.pontifex.Engine;
import org.wyx.diego.pontifex.FilterChain;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.exception.ExceptionCode;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.holder.UnifyPipelineHolderInstance;
import org.wyx.diego.pontifex.pipeline.Pipeline;

import javax.annotation.Resource;

/**
 * @author diego
 * @time 2015-07-11
 * @description
 */
public class PontifexMainFilter extends AbstractFilter {

    @Resource
    private Engine engine;

    @Override
    void before(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse) {

    }

    @Override
    void after(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse) {

    }

    @Override
    public void doFilter(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse, FilterChain filterChain) {

        before(pontifexRequest, pontifexResponse);

        Pipeline pipeline = UnifyPipelineHolderInstance.INSTANCE.obtain(pontifexRequest);

        if(pipeline == null) throw new PontifexRuntimeException(ExceptionCode.EXCEPTION_CODE_PL_INEXISTENCE);

        engine.launch(pipeline, pontifexRequest, pontifexResponse);

        after(pontifexRequest, pontifexResponse);

    }

}
