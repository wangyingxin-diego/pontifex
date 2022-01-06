package org.wyx.diego.pontifex.cache;

import org.wyx.diego.pontifex.annotation.Cache;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangyingxin
 * @title: CacheManager
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/21
 */
public class CacheManager {

    private static final ConcurrentHashMap<String, Object> pipelineMap = new ConcurrentHashMap();

    public CacheManager() {
    }

    public static void addPipeline(String pipelineName, Cache cache) {
        Object object = pipelineMap.get(pipelineName);
    }

    private CacheHolder genBaseCacheHolder(Cache cache) {
        CacheBean cacheBean = new CacheBean(cache);
        List<Target> targets = cacheBean.getTargets();
        Iterator var4 = targets.iterator();
        if (var4.hasNext()) {
            Target target = (Target)var4.next();
            switch(target) {
                case Memory:
                case Redis:
                default:
                    throw new IllegalArgumentException();
            }
        } else {
            return null;
        }
    }

    private GuavaCacheHolder getMemeryHolder(CacheBean cacheBean) {
        new GuavaCacheHolder(cacheBean);
        return null;
    }

}
