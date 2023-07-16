package org.wyx.diego.pontifex.cache;

public interface GetKey<T extends Key> {

    String KEY_PRE = "pontifex";
    String SP = "-";

    String getKey(T param);

}
