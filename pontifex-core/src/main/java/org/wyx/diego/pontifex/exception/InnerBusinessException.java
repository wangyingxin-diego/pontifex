package org.wyx.diego.pontifex.exception;

public enum InnerBusinessException implements BusinessException {

    INNER_BUSINESS_EXCEPTION_PARAM_ERROR(9000000, "参数错误", ExceptionLevel.EXCEPTION_MUST),
    INNER_BUSINESS_EXCEPTION_DECRYPT_ERROR(9000001, "解密错误", ExceptionLevel.EXCEPTION_MUST),
    INNER_BUSINESS_EXCEPTION__SERVICE_ERROR(ExceptionCode.EXCEPTION_CODE_BUSINESS_ERROR.getCode(), ExceptionCode.EXCEPTION_CODE_BUSINESS_ERROR.getMsg(), ExceptionLevel.EXCEPTION_MUST),

    ;

    private int code;

    private String msg;

    private ExceptionLevel exceptionLevel;

    InnerBusinessException(int code, String msg, ExceptionLevel exceptionLevel) {
        this.code = code;
        this.msg = msg;
        this.exceptionLevel = exceptionLevel;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public ExceptionLevel getExceptionLevel() {
        return this.exceptionLevel;
    }


}
