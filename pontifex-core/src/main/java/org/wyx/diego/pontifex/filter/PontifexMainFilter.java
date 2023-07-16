package org.wyx.diego.pontifex.filter;


import org.wyx.diego.pontifex.exception.ExceptionCode;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.loader.SequencePipelineLoaderInstance;
import org.wyx.diego.pontifex.pipeline.Pipeline;
import org.wyx.diego.pontifex.Engine;
import org.wyx.diego.pontifex.FilterChain;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;

/**
 * @author diego
 * @time 2015-07-11
 * @description
 */
public class PontifexMainFilter extends AbstractFilter {

    private Engine engine;

    public PontifexMainFilter(Engine engine) {
        this.engine = engine;
    }

    @Override
    void before(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse) {

    }

    @Override
    void after(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse) {

    }

    @Override
    public void doFilter(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse, FilterChain filterChain) {


        Pipeline pipeline = SequencePipelineLoaderInstance.INSTANCE.getPipeline(pontifexRequest);

        if(pipeline == null) throw new PontifexRuntimeException(ExceptionCode.EXCEPTION_CODE_PL_INEXISTENCE);

        filterChain.doBefore(pontifexRequest, pontifexResponse);

        engine.launch(pipeline, pontifexRequest, pontifexResponse);

        filterChain.doAfter(pontifexRequest, pontifexResponse);

    }

}
