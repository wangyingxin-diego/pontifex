package org.wyx.diego.pontifex;

import java.io.Serializable;

public class WebRequest implements Serializable {

    private String ip;

    public String getIp() {
        return ip;
    }

    public WebRequest setIp(String ip) {
        this.ip = ip;
        return this;
    }
}
