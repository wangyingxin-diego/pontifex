package org.wyx.diego.pontifex.cache;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.Request;

public enum DefaultPipelineGetKey implements GetKey<PontifexRequest> {
    DEFAULT_GET_KEY;

    private static final String PRE = "pipeline";
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPipelineGetKey.class);


    @Override
    public String getKey(PontifexRequest pontifexRequest) {

        String key = pontifexRequest.getRequestPayload().getPipelineCacheKey();
        if(key != null) {
            return key;
        }

        String bizKey = pontifexRequest.getBizKey();
        Request request = pontifexRequest.getBizObject();
        StringBuilder sb = new StringBuilder();
        sb.append(KEY_PRE);
        sb.append(SP);
        sb.append(PRE);
        sb.append(SP);
        sb.append(bizKey);
        sb.append(SP);
        sb.append(JSONObject.toJSONString(request));
        key = sb.toString();

        LOGGER.info("pontifex pipelineName={}, cacheKey={}", pontifexRequest.getBizKey(), key);

        pontifexRequest.getRequestPayload().setPipelineCacheKey(key);

        return key;

    }
}
