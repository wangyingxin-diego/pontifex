package org.wyx.diego.pontifex.flow;

import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.flow.annotation.FlowDeGrade;

import java.util.function.Function;

public interface FlowDeGradePipelineInterface {

    void init(String name, FlowDeGrade flowDeGrade);

    PontifexResponse<?> invoke(PontifexRequest pontifexRequest, Function<PontifexRequest<?>, PontifexResponse<?>> function, Function<PontifexRequest<?>, PontifexResponse<?>> fallback);

}
