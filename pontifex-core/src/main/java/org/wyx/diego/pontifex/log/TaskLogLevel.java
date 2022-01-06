package org.wyx.diego.pontifex.log;

/**
 * @author wangyingxin
 * @title: TaskLogLevel
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/31
 */
public enum TaskLogLevel {

    SIMPLE(1, "simple"),
    PAYLOAD(100, "payload"),
    ALL(1000, "all");

    private int level;
    private String desc;

    private TaskLogLevel(int level, String desc) {
        this.level = level;
        this.desc = desc;
    }

    public int getLevel() {
        return this.level;
    }

    public String getDesc() {
        return this.desc;
    }
}
