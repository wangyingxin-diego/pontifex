package org.wyx.diego.pontifex.exception;

/**
 * @author diego
 * @time 2015-09-30
 * @description
 */
public enum ExceptionCode {


    EXCEPTION_CODE_DEFAULT_VALUE(0, "", ExceptionType.BUSINESS_EXCEPTION, ExceptionLevel.EXCEPTION_DEFAULT_VALUE),
    EXCEPTION_CODE_BUSINESS_ERROR(500, "business error", ExceptionType.BUSINESS_EXCEPTION, ExceptionLevel.EXCEPTION_MUST),

    EXCEPTION_CODE_PL_TYPE(11, "pipeline type error", ExceptionType.PONTIFEX_EXCETION, ExceptionLevel.EXCEPTION_MUST),
    EXCEPTION_CODE_PL_LOAD_ERROR(100, "pipeline load error", ExceptionType.PONTIFEX_EXCETION, ExceptionLevel.EXCEPTION_MUST),
    EXCEPTION_CODE_PL_NAME_NULL(101, "pipeline name can not null or null character string", ExceptionType.PONTIFEX_EXCETION, ExceptionLevel.EXCEPTION_MUST),
    EXCEPTION_CODE_PL_INEXISTENCE(102, "pipeline inexistence", ExceptionType.PONTIFEX_EXCETION, ExceptionLevel.EXCEPTION_MUST),

    EXCEPTION_CODE_TASK(1000, "task exception", ExceptionType.TASK_EXCEPTION, ExceptionLevel.EXCEPTION_MUST),
    EXCEPTION_CODE_TASK_ANNO_NULL(1001, "task annotation is null", ExceptionType.TASK_EXCEPTION, ExceptionLevel.EXCEPTION_MUST),
    EXCEPTION_CODE_TASK_META(1002, "", ExceptionType.TASK_EXCEPTION, ExceptionLevel.EXCEPTION_MUST),


    EXCEPTION_CODE_COMMPONT_GET(10001, "commpont get exception", ExceptionType.COMPONENT_EXCEPTION, ExceptionLevel.EXCEPTION_MUST);
    ;

    private int code;

    private String msg;

    private ExceptionType exceptionType;

    private ExceptionLevel exceptionLevel;

    ExceptionCode(int code, String msg, ExceptionType exceptionType, ExceptionLevel exceptionLevel) {
        this.code = code;
        this.msg = msg;
        this.exceptionType = exceptionType;
        this.exceptionLevel = exceptionLevel;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    public ExceptionLevel getExceptionLevel() {
        return exceptionLevel;
    }}
