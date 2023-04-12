package org.wyx.diego.pontifex.demo;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.wyx.diego.pontifex.spring.request.PontifexParam;
import org.wyx.diego.pontifex.spring.request.PontifexParamHandler;

@Component
public class DDPontifexParamHandler implements PontifexParamHandler {

    @Override
    public void handle(PontifexParam pontifexParam) {

        pontifexParam.getPontifexRequest().setSecretKey("1234567890123456");
        System.out.println(JSONObject.toJSONString(pontifexParam.getPontifexRequest()));

    }
}
