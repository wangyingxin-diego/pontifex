package org.wyx.diego.pontifex.log;

/**
 * @author wangyingxin
 * @title: ComponentLogLevel
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/31
 */
public enum ComponentLogLevel {

    SIMPLE(1, "simple"),
    ALL(10000, "all");

    private int level;
    private String desc;

    private ComponentLogLevel(int level, String desc) {
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
