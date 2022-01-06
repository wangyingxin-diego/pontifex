package org.wyx.diego.pontifex.loader.handler.invoke;

import java.util.ArrayList;
import java.util.List;

/**
 * @author diego
 * @time 2015-10-24
 * @description
 */
public class LogTaskContext {
    private List<LogTask> logTasks = new ArrayList();

    public LogTaskContext() {
    }

    public static LogTaskContext newInstance() {
        return new LogTaskContext();
    }

    public void addLogTask(LogTask logTask) {
        if (logTask != null) {
            this.logTasks.add(logTask);
        }
    }

    public List<LogTask> getLogTasks() {
        return this.logTasks;
    }

    public LogTaskContext setLogTasks(List<LogTask> logTasks) {
        this.logTasks = logTasks;
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < this.logTasks.size(); ++i) {
            if (i != 0) {
                sb.append("-").append(this.logTasks.get(i));
            } else {
                sb.append(this.logTasks.get(i));
            }
        }

        return sb.toString();
    }
}
