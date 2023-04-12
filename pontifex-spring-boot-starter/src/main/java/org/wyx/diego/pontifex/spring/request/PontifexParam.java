package org.wyx.diego.pontifex.spring.request;

import org.springframework.web.context.request.ServletWebRequest;
import org.wyx.diego.pontifex.PontifexRequest;


public class PontifexParam {

    private PontifexRequest pontifexRequest;

    private ServletWebRequest servletWebRequest;

    public PontifexParam(PontifexRequest pontifexRequest, ServletWebRequest servletWebRequest) {
        this.pontifexRequest = pontifexRequest;
        this.servletWebRequest = servletWebRequest;
    }

    public PontifexRequest getPontifexRequest() {
        return pontifexRequest;
    }

    public ServletWebRequest getServletWebRequest() {
        return servletWebRequest;
    }

}
