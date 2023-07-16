package org.wyx.diego.pontifex.filter;

import org.wyx.diego.pontifex.cache.CacheManager;
import org.wyx.diego.pontifex.cache.CacheManagerInstance;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.FilterChain;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.Response;
import org.wyx.diego.pontifex.cache.CacheConfig;

/**
 * @author wangyingxin
 * @title: CacheFilter
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/1
 */
public class CacheFilter extends AbstractFilter {

    void before(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse) {

        boolean enabledCache = enabledCache();
        if(!enabledCache) {
            return;
        }

        Object object = CacheManagerInstance.INSTANCE.getCacheManager().get(pontifexRequest.getBizKey(), pontifexRequest);
        if(object == null) {
            return;
        }

        pontifexResponse.setResult((Response) object);

        throw PontifexRuntimeException.DEFAULT_VALUE_EXCEPTION;

    }

    void after(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse) {

        boolean enabledCache = enabledCache();
        if(!enabledCache && pontifexResponse.getMeta().getCode() == 0 && pontifexResponse.getResult() != null) {
            return;
        }

        pontifexRequest.getBizKey();

        CacheManagerInstance.INSTANCE.getCacheManager().put(pontifexRequest.getBizKey(), pontifexResponse.getResult(), pontifexRequest);

    }

    private boolean enabledCache() {
        CacheManager cacheManager = CacheManagerInstance.INSTANCE.getCacheManager();
        CacheConfig config = cacheManager.getCacheConfig();
        if(config == null || !config.open()) {
            return false;
        }

        return true;

    }

    public void doFilter(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse, FilterChain filterChain) {
    }

}
