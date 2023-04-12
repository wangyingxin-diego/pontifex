package org.wyx.diego.pontifex.demo.business.exception;

import org.wyx.diego.pontifex.exception.BusinessException;
import org.wyx.diego.pontifex.exception.ExceptionLevel;

public class MyPontifexException implements BusinessException {

    @Override
    public int getCode() {
        return 33330;
    }

    @Override
    public String getMsg() {
        return "参数错了";
    }

    @Override
    public ExceptionLevel getExceptionLevel() {
        return ExceptionLevel.EXCEPTION_MUST;
    }
}
