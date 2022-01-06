package org.wyx.diego.pontifex.pipeline;


import org.wyx.diego.pontifex.Request;
import org.wyx.diego.pontifex.Response;
import org.wyx.diego.pontifex.loader.handler.invoke.LogTaskContext;

/**
 * @author diego
 * @time 2015-07-11
 * @description
 */
public class TaskContext<Req extends Request, PPayload extends Payload, Res extends Response> extends PLContext<Req, PPayload, Res> {

    private LogTaskContext logTaskContext;

    private LogTaskContext logTaskContext4Component;

    public LogTaskContext getLogTaskContext() {
        return this.logTaskContext;
    }

    public TaskContext<Req, PPayload, Res> setLogTaskContext(LogTaskContext logTaskContext) {
        this.logTaskContext = logTaskContext;
        return this;
    }

    public LogTaskContext getLogTaskContext4Component() {
        return this.logTaskContext4Component;
    }

    public TaskContext<Req, PPayload, Res> setLogTaskContext4Component(LogTaskContext logTaskContext4Component) {
        this.logTaskContext4Component = logTaskContext4Component;
        return this;
    }
}
