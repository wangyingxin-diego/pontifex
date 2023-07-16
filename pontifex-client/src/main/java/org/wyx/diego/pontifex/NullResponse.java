package org.wyx.diego.pontifex;

public class NullResponse implements Response {

    private String result = "result is null";

    public NullResponse() {
    }

    public String getResult() {
        return result;
    }

    public NullResponse setResult(String result) {
        this.result = result;
        return this;
    }
}
