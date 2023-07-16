package org.wyx.diego.pontifex.context;

import org.wyx.diego.pontifex.pipeline.PipelineManager;
import org.wyx.diego.pontifex.pipeline.PipelineManagerInstance;

public class PipelineContext {

    private PipelineManager pipelineManager = PipelineManagerInstance.INSTANCE.getPipelineManager();

    public PipelineManager getPipelineManager() {
        return pipelineManager;
    }
}
