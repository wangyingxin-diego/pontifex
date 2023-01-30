package org.wyx.diego.pontifex.cache;

import java.util.concurrent.ExecutionException;

public class RedisCacheHolder extends BaseCacheHolder {


    public RedisCacheHolder(CacheHolder parent) {

        super.parent = parent;

    }

    @Override
    public Object get(String key) {
        if(key == null || "".equals(key.trim())) {
            return null;
        }
        try {
            if(parent != null) {
                return parent.get(key);
            }
            return null;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }



}
