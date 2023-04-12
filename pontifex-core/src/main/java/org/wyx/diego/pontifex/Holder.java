package org.wyx.diego.pontifex;

public interface Holder<P, R> {
    R obtain(P p);
}
