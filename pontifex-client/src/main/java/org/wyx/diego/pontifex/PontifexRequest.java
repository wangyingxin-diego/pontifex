package org.wyx.diego.pontifex;

import org.wyx.diego.pontifex.cache.Key;
import org.wyx.diego.pontifex.encrypt.EncryptDecryptInterface;
import org.wyx.diego.pontifex.pipeline.PipelineType;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PontifexRequest<P extends Request> implements Serializable, Key, EncryptDecryptInterface {

    @NotBlank
    private String bizKey;

    @NotNull
    @Valid
    private P bizObject;

    private PipelineType pipelineType = PipelineType.PIPELINE_TYPE_SEQUENCE;

    private String traceId;

    private final Map<String, String> headers = new HashMap<>();

    private String decryptKey;

    private boolean  decryptSwitch = false;

    private String encryptKey;

    private boolean  encryptSwitch = false;

    private final RequestPayload requestPayload = new RequestPayload();

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

    public String getDecryptKey() {
        return decryptKey;
    }

    public PontifexRequest<P> setDecryptKey(String decryptKey) {
        this.decryptKey = decryptKey;
        return this;
    }

    public boolean isDecryptSwitch() {
        return decryptSwitch;
    }

    public PontifexRequest<P> setDecryptSwitch(boolean decryptSwitch) {
        this.decryptSwitch = decryptSwitch;
        return this;
    }

    public RequestPayload getRequestPayload() {
        return requestPayload;
    }

    public boolean isEncryptSwitch() {
        return encryptSwitch;
    }

    public PontifexRequest<P> setEncryptSwitch(boolean encryptSwitch) {
        this.encryptSwitch = encryptSwitch;
        return this;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public PontifexRequest<P> setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
        return this;
    }
}
