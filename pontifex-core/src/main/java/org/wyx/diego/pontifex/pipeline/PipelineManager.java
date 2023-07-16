package org.wyx.diego.pontifex.pipeline;

import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.loader.PipelineLoader;
import org.wyx.diego.pontifex.loader.SequencePipelineLoaderInstance;

import java.util.concurrent.ConcurrentHashMap;

import static org.wyx.diego.pontifex.exception.ExceptionCode.EXCEPTION_CODE_PL_META_REPETITION;

public class PipelineManager {

    private PipelineLoader pipelineLoader = SequencePipelineLoaderInstance.INSTANCE;

    private ConcurrentHashMap<String, PipelineInterface> interfaceConcurrentHashMap = new ConcurrentHashMap<>();

    public Pipeline getPipeline(PontifexRequest pontifexRequest) {
        return pipelineLoader.getPipeline(pontifexRequest);
    }

    public PipelineInterface getPipelineInterface(String pipelineName) {
        if(pipelineName == null || "".equals(pipelineName.trim())) {
            return null;
        }
        return interfaceConcurrentHashMap.get(pipelineName);
    }

    public void addPipelineInterface(String pipelineName, PipelineInterface pipelineInterface) {
        if(pipelineName == null || "".equals(pipelineName.trim()) || pipelineInterface == null) {
            return;
        }
        PipelineInterface pre = interfaceConcurrentHashMap.putIfAbsent(pipelineName, pipelineInterface);
        if(pre != null) {
            PontifexRuntimeException.exception(EXCEPTION_CODE_PL_META_REPETITION);
        }
    }

}
