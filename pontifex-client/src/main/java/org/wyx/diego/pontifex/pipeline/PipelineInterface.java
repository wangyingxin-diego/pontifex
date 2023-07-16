package org.wyx.diego.pontifex.pipeline;

import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.Request;
import org.wyx.diego.pontifex.Response;
import org.wyx.diego.pontifex.cache.CacheInterface;
import org.wyx.diego.pontifex.encrypt.EncryptDecryptInterface;

public interface PipelineInterface<Req extends Request, Res extends Response> extends CacheInterface<PontifexRequest>, EncryptDecryptInterface {

    PontifexResponse<Res> call(PontifexRequest<Req> pontifexRequest);

    PontifexResponse<Res> fallback(PontifexRequest<Req> pontifexRequest);

}
