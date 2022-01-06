package org.wyx.diego.pontifex.spring.manager;

import org.springframework.stereotype.Component;
import org.wyx.diego.pontifex.IPontifexManager;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.manager.DefaultPontifexManagerInstance;

/**
 * @author wangyingxin
 * @title: SpringPontifexManager
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/29
 */
@Component
public class SpringPontifexManager implements IPontifexManager {

    @Override
    public PontifexResponse handler(PontifexRequest pontifexRequest) {
        return DefaultPontifexManagerInstance.INSTANCE.handler(pontifexRequest);
    }
}
