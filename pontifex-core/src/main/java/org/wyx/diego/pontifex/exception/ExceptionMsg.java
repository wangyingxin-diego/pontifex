package org.wyx.diego.pontifex.exception;

/**
 * @author wangyingxin
 * @title: ExceptionMsg
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/1
 */
class ExceptionMsg {
    private final int code;
    private final String msg;
    private final ExceptionType exceptionType = ExceptionType.BUSINESS_EXCEPTION;
    private final ExceptionLevel exceptionLevel;

    public ExceptionMsg(int code, String msg) {
        this.exceptionLevel = ExceptionLevel.EXCEPTION_MUST;
        this.code = code;
        this.msg = msg;
    }

    public ExceptionMsg(int code, String msg, ExceptionLevel exceptionLevel) {
        if(exceptionLevel != null) {
            this.exceptionLevel = exceptionLevel;
        } else {
            this.exceptionLevel = ExceptionLevel.EXCEPTION_MUST;
        }
        this.code = code;
        this.msg = msg;
    }

    public ExceptionMsg(BusinessException businessException) {
        if(businessException.getExceptionLevel() == null) {
            this.exceptionLevel = ExceptionLevel.EXCEPTION_MUST;
        } else {
            this.exceptionLevel = businessException.getExceptionLevel();
        }
        int code = businessException.getCode();
        this.msg = businessException.getMsg();
        if (code < 0) {
            this.code = ExceptionCode.EXCEPTION_CODE_BUSINESS_ERROR.getCode();
        } else {
            this.code = code;
        }
    }

    public ExceptionMsg(BusinessException businessException, ExceptionLevel exceptionLevel) {
        if(exceptionLevel != null) {
            this.exceptionLevel = exceptionLevel;
        } else {
            this.exceptionLevel = ExceptionLevel.EXCEPTION_MUST;
        }
        int code = businessException.getCode();
        this.msg = businessException.getMsg();
        if (code < 0) {
            this.code = ExceptionCode.EXCEPTION_CODE_BUSINESS_ERROR.getCode();
        } else {
            this.code = code;
        }
    }



    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public ExceptionType getExceptionType() {
        return this.exceptionType;
    }

    public ExceptionLevel getExceptionLevel() {
        return this.exceptionLevel;
    }
}
