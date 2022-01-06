package org.wyx.diego.pontifex.pipeline;


import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.Request;
import org.wyx.diego.pontifex.Response;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author diego
 * @time 2015-07-10
 * @description
 */
public class PLContext<Req extends Request, PPayload extends Payload, Res extends Response> extends ConcurrentHashMap<String, Object> {

    private PontifexRequest<Req> pontifexRequest;
    private PontifexResponse<Res> pontifexResponse;
    private PPayload payload;

    public void run(Pipeline p) {
        Pipeline.run((TaskContext) this, p);
    }

    public Res getPontifexResult() {

        return this.pontifexResponse.getResult();

    }

    public void putPontifexResult(Res res) {

        pontifexResponse.setResult(res);

    }

    @Override
    public Object put(String key, Object value) {

        if(value == null) {

            value = new Object();

        }
        return super.put(key, value);

    }

    public void putContextObject(Object contextObject) {

        super.put("pontifex.contextObject", contextObject);

    }

    public Object getContextObject() {

        return super.get("pontifex.contextObject");

    }

    public PLContext<Req, PPayload, Res> setPontifexRequest(PontifexRequest<Req> pontifexRequest) {
        this.pontifexRequest = pontifexRequest;
        return this;
    }

    public PontifexRequest<Req> getPontifexRequest() {
        return pontifexRequest;
    }

    public PontifexResponse<Res> getPontifexResponse() {
        return pontifexResponse;
    }

    public PLContext<Req, PPayload, Res> setPontifexResponse(PontifexResponse<Res> pontifexResponse) {
        this.pontifexResponse = pontifexResponse;
        return this;
    }

    public PPayload getPayload() {
        return this.payload;
    }

    void setPayload(PPayload payload) {
        this.payload = payload;
    }
}
