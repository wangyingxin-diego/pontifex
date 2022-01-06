package org.wyx.diego.pontifex;

/**
 * @author diego
 * @time 2015-07-11
 * @description
 */
public interface Loader<P, R> {
    R load(P p);
}
