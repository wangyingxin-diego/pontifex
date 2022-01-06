package org.wyx.diego.pontifex;

public enum ModuleType {

    MODULE_TYPE_TASK(1, "task"),
    MODULE_TYPE_COMPONENT(2, "component"),
    ;

    private int type;

    private String desc;

    ModuleType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }}
