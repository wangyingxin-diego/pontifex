package org.wyx.diego.pontifex.pipeline;

/**
 * @author diego
 * @time 2015-10-11
 * @description
 */
public interface PTask<T extends PLContext> {

    void run(T ctx);

    int getSort();

    void setSort(int sort);

}
