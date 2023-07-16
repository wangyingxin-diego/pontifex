package org.wyx.diego.pontifex.cache;

import java.io.Serializable;

public interface CacheConfig extends Serializable {


    boolean open();

    long maxMemorySize();

    long timeout();


}
