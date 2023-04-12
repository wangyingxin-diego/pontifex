package org.wyx.diego.pontifex;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author diego
 * @time 2015-07-16
 * @description
 */
public class PontifexResponse<T extends Response> implements Serializable {

    private Meta meta = new Meta();
    private T result;
    private Pagination pagination = new Pagination();
    private Map<String, Object> payload = new HashMap<>();

    public Meta getMeta() {
        return meta;
    }

    public T getResult() {
        return result;
    }

    public PontifexResponse<T> setResult(T result) {
        this.result = result;
        return this;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public PontifexResponse<T> setPagination(Pagination pagination) {
        this.pagination = pagination;
        return this;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public PontifexResponse<T> setPayload(Map<String, Object> payload) {
        this.payload = payload;
        return this;
    }
}
