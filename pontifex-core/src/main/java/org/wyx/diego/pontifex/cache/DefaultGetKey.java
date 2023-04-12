package org.wyx.diego.pontifex.cache;

import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.Request;

public enum DefaultGetKey implements GetKey {

    DEFAULT_GET_KEY;

    @Override
    public String getKey(PontifexRequest pontifexRequest) {

        String bizKey = pontifexRequest.getBizKey();
        Request request = pontifexRequest.getBizObject();
        return "";
    }
}
