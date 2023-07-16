package org.wyx.diego.pontifex;

import java.io.Serializable;

public class Meta implements Serializable {

    private int code;

    private String message;

    public int getCode() {
        return code;
    }

    public Meta setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Meta setMessage(String message) {
        this.message = message;
        return this;
    }
}
