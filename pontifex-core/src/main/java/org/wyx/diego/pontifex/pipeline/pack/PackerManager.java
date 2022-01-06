package org.wyx.diego.pontifex.pipeline.pack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author diego
 * @time 2015-10-18
 * @description
 */
public class PackerManager {

    private List packerList = new ArrayList();

    public void manager() {



    }

    public static PackerManager newInstance() {

        PackerManager packerManager = new PackerManager();
        packerManager.packerList.add(new TaskTimePacker());
        return packerManager;

    }

}
