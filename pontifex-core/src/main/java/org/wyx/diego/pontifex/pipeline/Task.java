package org.wyx.diego.pontifex.pipeline;

import org.wyx.diego.pontifex.Request;
import org.wyx.diego.pontifex.Response;

/**
 * @author diego
 * @time 2015-07-11
 * @description
 */
public abstract class Task<Req extends Request, PPayload extends Payload, Res extends Response> extends PLTask<TaskContext<Req, PPayload, Res>> {



}
