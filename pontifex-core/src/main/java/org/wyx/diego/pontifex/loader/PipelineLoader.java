package org.wyx.diego.pontifex.loader;


import org.wyx.diego.pontifex.Loader;
import org.wyx.diego.pontifex.pipeline.Pipeline;
import org.wyx.diego.pontifex.PontifexRequest;

/**
 * @author diego
 * @time 2015-07-11
 * @description
 */
public interface PipelineLoader<P, R> extends Loader<P, R> {

    Pipeline getPipeline(PontifexRequest pontifexRequest);

}
