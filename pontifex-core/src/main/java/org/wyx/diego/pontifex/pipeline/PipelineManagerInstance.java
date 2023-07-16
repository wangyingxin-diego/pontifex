package org.wyx.diego.pontifex.pipeline;

public enum PipelineManagerInstance {
    INSTANCE(new PipelineManager());


    private PipelineManager pipelineManager;

    PipelineManagerInstance(PipelineManager pipelineManager) {
        this.pipelineManager = pipelineManager;
    }

    public PipelineManager getPipelineManager() {
        return this.pipelineManager;
    }

}
