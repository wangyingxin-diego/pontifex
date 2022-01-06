package org.wyx.diego.pontifex;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class PontifexRequest<P extends Request> implements Serializable {

    @NotBlank
    private String bizKey;

    private P bizObject;

    private int pipelineType = 1;

    private String traceId;

    public String getBizKey() {
        return bizKey;
    }

    public void setBizKey(String bizKey) {
        this.bizKey = bizKey;
    }

    public P getBizObject() {
        return bizObject;
    }

    public PontifexRequest<P> setBizObject(P bizObject) {
        this.bizObject = bizObject;
        return this;
    }

    public int getPipelineType() {
        return pipelineType;
    }

    public void setPipelineType(int pipelineType) {
        this.pipelineType = pipelineType;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
