package org.wyx.diego.pontifex;

import org.wyx.diego.pontifex.pipeline.PipelineType;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PontifexRequest<P extends Request> implements Serializable {

    @NotBlank
    private String bizKey;

    @NotNull
    @Valid
    private P bizObject;

    private PipelineType pipelineType = PipelineType.PIPELINE_TYPE_SEQUENCE;

    private String traceId;

    private Map<String, String> headers = new HashMap<>();

    private String secretKey;

    private boolean  decryptSwitch = false;

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

    public PipelineType getPipelineType() {
        return pipelineType;
    }

    public PontifexRequest<P> setPipelineType(PipelineType pipelineType) {
        this.pipelineType = pipelineType;
        return this;
    }

    public String getTraceId() {
        return traceId;
    }

    public PontifexRequest<P> setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public PontifexRequest<P> setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public boolean isDecryptSwitch() {
        return decryptSwitch;
    }

    public PontifexRequest<P> setDecryptSwitch(boolean decryptSwitch) {
        this.decryptSwitch = decryptSwitch;
        return this;
    }
}
