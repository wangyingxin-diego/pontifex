package org.wyx.diego.pontifex.spring.exception;

import org.wyx.diego.pontifex.DefaultFailResponse;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.exception.InnerBusinessException;

public class DefaultExcludePontifexExceptionHandler implements ExcludePontifexExceptionHandler {

    @Override
    public Object handler(Exception exception) {

        PontifexResponse pontifexResponse = new PontifexResponse();
        pontifexResponse.getMeta().setCode(InnerBusinessException.INNER_BUSINESS_EXCEPTION__SERVICE_ERROR.getCode()).setMessage(InnerBusinessException.INNER_BUSINESS_EXCEPTION__SERVICE_ERROR.getMsg());
        pontifexResponse.setResult(DefaultFailResponse.DEFAULT_FAIL_RESPONSE.getDefaultFailResponseVal());
        return pontifexResponse;
    }
}
