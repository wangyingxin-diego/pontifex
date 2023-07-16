package org.wyx.diego.pontifex;

public enum DefaultFailResponse {

    DEFAULT_FAIL_RESPONSE(new DefaultFailResponseVal());

    private DefaultFailResponseVal defaultFailResponseVal;


    DefaultFailResponse(DefaultFailResponseVal defaultFailResponseVal) {
        this.defaultFailResponseVal = defaultFailResponseVal;
    }

    public static class DefaultFailResponseVal implements Response {

        int code = 500;

        public int getCode() {
            return code;
        }

        public DefaultFailResponseVal setCode(int code) {
            this.code = code;
            return this;
        }
    }

    public DefaultFailResponseVal getDefaultFailResponseVal() {
        return defaultFailResponseVal;
    }
}
