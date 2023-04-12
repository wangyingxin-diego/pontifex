package org.wyx.diego.pontifex;

public abstract class BaseRequest implements Request {

    private String bizSign;

    public String getBizSign() {
        return bizSign;
    }

    public BaseRequest setBizSign(String bizSign) {
        this.bizSign = bizSign;
        return this;
    }

}
