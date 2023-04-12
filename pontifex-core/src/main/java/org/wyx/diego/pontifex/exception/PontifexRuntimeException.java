package org.wyx.diego.pontifex.exception;

/**
 * @author diego
 * @time 2015-07-10
 * @description
 */
public class PontifexRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final PontifexRuntimeException DEFAULT_VALUE_EXCEPTION;

    public final int errorCode;
    public final String debugMsg;
    public final String userMsg;
    public final ExceptionType exceptionType;
    public final ExceptionLevel exceptionLevel;


    public PontifexRuntimeException(int errorCode, String debugMsg, String userMsg, ExceptionType exceptionType, ExceptionLevel exceptionLevel, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.debugMsg = debugMsg;
        this.userMsg = userMsg;
        this.exceptionType = exceptionType;
        this.exceptionLevel = exceptionLevel;

    }

    public PontifexRuntimeException(int errorCode, String debugMsg, String userMsg, ExceptionType exceptionType, ExceptionLevel exceptionLevel) {
        this.errorCode = errorCode;
        this.debugMsg = debugMsg;
        this.userMsg = userMsg;
        this.exceptionType = exceptionType;
        this.exceptionLevel = exceptionLevel;

    }

    public PontifexRuntimeException(int errorCode, String userMsg, ExceptionType exceptionType, ExceptionLevel exceptionLevel) {
        this.errorCode = errorCode;
        this.debugMsg = null;
        this.userMsg = userMsg;
        this.exceptionType = exceptionType;
        this.exceptionLevel = exceptionLevel;

    }

    public PontifexRuntimeException(ExceptionCode exceptionCode) {
        this.errorCode = exceptionCode.getCode();
        this.debugMsg = null;
        this.userMsg = exceptionCode.getMsg();
        this.exceptionType = exceptionCode.getExceptionType();
        this.exceptionLevel = exceptionCode.getExceptionLevel();
    }

    public PontifexRuntimeException(ExceptionCode exceptionCode, Throwable cause) {
        super(cause);
        this.errorCode = exceptionCode.getCode();
        this.debugMsg = null;
        this.userMsg = exceptionCode.getMsg();
        this.exceptionType = exceptionCode.getExceptionType();
        this.exceptionLevel = exceptionCode.getExceptionLevel();
    }

    public PontifexRuntimeException(int errorCode, String userMsg, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.userMsg = userMsg;
        this.debugMsg = null;
        this.exceptionType = ExceptionType.PONTIFEX_EXCETION;
        this.exceptionLevel = ExceptionLevel.EXCEPTION_DEFAULT_VALUE;
    }

    public PontifexRuntimeException(int errorCode, String userMsg) {
        this.errorCode = errorCode;
        this.userMsg = userMsg;
        this.debugMsg = null;
        this.exceptionType = ExceptionType.PONTIFEX_EXCETION;
        this.exceptionLevel = ExceptionLevel.EXCEPTION_DEFAULT_VALUE;
    }

    public PontifexRuntimeException(ExceptionMsg exceptionMsg) {
        this.errorCode = exceptionMsg.getCode();
        this.debugMsg = null;
        this.userMsg = exceptionMsg.getMsg();
        this.exceptionType = exceptionMsg.getExceptionType();
        this.exceptionLevel = exceptionMsg.getExceptionLevel();
    }

    public static PontifexRuntimeException exception(ExceptionCode exceptionCode) {
        return new PontifexRuntimeException(exceptionCode);
    }

    protected static PontifexRuntimeException exception(ExceptionMsg exceptionMsg) {
        return new PontifexRuntimeException(exceptionMsg);
    }

    public static PontifexRuntimeException exception(BusinessException businessException) {
        return new PontifexRuntimeException(new ExceptionMsg(businessException));
    }

    public static PontifexRuntimeException exception(int errorCode, String userMsg) {
        return new PontifexRuntimeException(errorCode, userMsg);
    }

    static {
        DEFAULT_VALUE_EXCEPTION = new PontifexRuntimeException(ExceptionCode.EXCEPTION_CODE_DEFAULT_VALUE);
    }

}
