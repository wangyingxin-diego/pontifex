package org.wyx.diego.pontifex.demo;

import org.wyx.diego.pontifex.flow.config.FlowConfig;
import org.wyx.diego.pontifex.flow.sentinel.SentinelFlowDeGradePipelineInterface;

public class MyFlowConfig implements FlowConfig {

    @Override
    public Class flowClass() {
        return SentinelFlowDeGradePipelineInterface.class;
    }

}
