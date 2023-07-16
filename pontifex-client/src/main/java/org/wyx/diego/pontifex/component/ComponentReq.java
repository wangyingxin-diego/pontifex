package org.wyx.diego.pontifex.component;

import org.wyx.diego.pontifex.cache.Key;

/**
 * @author diego
 * @time 2015-07-16
 * @description
 */
public interface ComponentReq extends Key {

    @Deprecated
    String getKey();

}
