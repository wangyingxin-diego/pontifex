package org.wyx.diego.pontifex.context;

public class PontifexContext {

    private CacheContext cacheContext = new CacheContext();

    private PipelineContext pipelineContext = new PipelineContext();


    public CacheContext getCacheContext() {
        return cacheContext;
    }

    public PipelineContext getPipelineContext() {
        return pipelineContext;
    }

}
