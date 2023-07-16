package org.wyx.diego.pontifex;

import java.io.Serializable;

public class RequestPayload implements Serializable {

    private final WebRequest webRequest = new WebRequest();

    private String pipelineCacheKey;


    public WebRequest getWebRequest() {
        return webRequest;
    }

    public String getPipelineCacheKey() {
        return pipelineCacheKey;
    }

    public RequestPayload setPipelineCacheKey(String pipelineCacheKey) {
        this.pipelineCacheKey = pipelineCacheKey;
        return this;
    }
}
