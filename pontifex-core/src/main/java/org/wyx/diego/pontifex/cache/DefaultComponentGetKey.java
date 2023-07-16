package org.wyx.diego.pontifex.cache;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.component.ComponentReq;

public enum DefaultComponentGetKey implements GetKey<ComponentReq> {

    DEFAULT_COMPONENT_GET_KEY;

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultComponentGetKey.class);
    private static final String PRE = "component";
    @Override
    public String getKey(ComponentReq param) {

        String key;
        StringBuilder sb = new StringBuilder();
        sb.append(KEY_PRE);
        sb.append(SP);
        sb.append(PRE);
        sb.append(SP);
        sb.append(JSONObject.toJSONString(param));
        key = sb.toString();
        return key;
    }
}
