package org.wyx.diego.pontifex.pipeline;

/**
 * @author wangyingxin
 * @title: PipelineType
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/30
 */
public enum PipelineType {

    PIPELINE_TYPE_SEQUENCE(1, "sequence"),
    PIPELINE_TYPE_DAG(2, "dag"),
    PIPELINE_TYPE_TREE(3, "tree"),
    ;

    private int value;
    private String desc;

    PipelineType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
