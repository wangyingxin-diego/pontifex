package org.wyx.diego.pontifex.cache;

public interface CacheInterface<T extends Key> {

    GetKey<T> getKey();


}
