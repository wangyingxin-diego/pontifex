package org.wyx.diego.pontifex.pipeline.pack;

import org.wyx.diego.pontifex.pipeline.PLTask;

/**
 * @author diego
 * @time 2015-10-18
 * @description
 */
public interface Packer {

    void before(PLTask task);

    void after(PLTask task);

}
