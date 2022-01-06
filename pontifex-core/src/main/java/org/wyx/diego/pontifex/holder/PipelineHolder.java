package org.wyx.diego.pontifex.holder;

import org.wyx.diego.pontifex.Holder;
import org.wyx.diego.pontifex.Loader;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.pipeline.PLTask;
import org.wyx.diego.pontifex.pipeline.Pipeline;
import org.wyx.diego.pontifex.pipeline.Task;

/**
 * @author diego
 * @time 2015-07-11
 * @description
 */
interface PipelineHolder extends Holder<PontifexRequest, Pipeline>, Loader<Task<?,?,?>, Task<?,?,?>> {

}
