package org.wyx.diego.pontifex;

import java.io.Serializable;

/**
 * @author diego
 * @time 2015-07-16
 * @description
 */
public class PontifexResponse<T extends Response> implements Serializable {

    private T result;
    private int code;
    private String status;
    private String reason;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
