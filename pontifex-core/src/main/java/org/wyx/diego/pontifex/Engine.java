package org.wyx.diego.pontifex;


import org.wyx.diego.pontifex.pipeline.Pipeline;

/**
 * @author diego
 * @time 2015-07-11
 * @description
 */
public interface Engine {

    void launch(Pipeline pipeline, PontifexRequest pontifexRequest, PontifexResponse pontifexResponse);

}
