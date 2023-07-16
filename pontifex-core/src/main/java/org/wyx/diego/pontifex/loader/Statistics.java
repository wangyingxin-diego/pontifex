package org.wyx.diego.pontifex.loader;

import org.wyx.diego.pontifex.loader.handler.invoke.LogTaskContext;

import java.util.ArrayList;
import java.util.List;

public class Statistics {

    private LogTaskContext logTaskContext = LogTaskContext.newInstance();

    private List<LogTaskContext> logTaskContexts = new ArrayList<>();


    public LogTaskContext getLogTaskContext() {
        return logTaskContext;
    }

    public void setLogTaskContext(LogTaskContext logTaskContext) {
        this.logTaskContext = logTaskContext;
    }

    public List<LogTaskContext> getLogTaskContexts() {
        return logTaskContexts;
    }

    public void setLogTaskContexts(List<LogTaskContext> logTaskContexts) {
        this.logTaskContexts = logTaskContexts;
    }
}
