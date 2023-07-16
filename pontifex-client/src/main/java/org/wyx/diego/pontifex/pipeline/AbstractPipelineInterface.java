package org.wyx.diego.pontifex.pipeline;

import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.Request;
import org.wyx.diego.pontifex.Response;
import org.wyx.diego.pontifex.cache.DefaultPipelineGetKey;
import org.wyx.diego.pontifex.cache.GetKey;

public abstract class AbstractPipelineInterface<Req extends Request, Res extends Response> implements PipelineInterface {

    @Override
    public boolean isDecryptSwitch() {
        return false;
    }

    @Override
    public String getDecryptKey() {
        return null;
    }

    @Override
    public boolean isEncryptSwitch() {
        return false;
    }

    @Override
    public String getEncryptKey() {
        return null;
    }

    @Override
    public GetKey<PontifexRequest> getKey() {
        return DefaultPipelineGetKey.DEFAULT_GET_KEY;
    }

}
