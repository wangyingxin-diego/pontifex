package org.wyx.diego.pontifex.spring.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wyx.diego.pontifex.exception.InnerBusinessException;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.DefaultFailResponse;
import org.wyx.diego.pontifex.PontifexResponse;


@ControllerAdvice
public class GlobalDefaultExceptionHandler {


    private ExcludePontifexExceptionHandler exceptionHandler;

    public GlobalDefaultExceptionHandler(ExcludePontifexExceptionHandler exceptionHandler) {
        if(exceptionHandler == null) {
            this.exceptionHandler = new DefaultExcludePontifexExceptionHandler();
        } else {
            this.exceptionHandler = exceptionHandler;
        }
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public PontifexResponse validExceptionHandler(MethodArgumentNotValidException bindException) {


        PontifexResponse pontifexResponse = new PontifexResponse();
        ObjectError objectError = bindException.getBindingResult().getAllErrors().get(0);
        String[] objects = objectError.getCodes();
        String fieldName = "";
        if(objects != null && objects.length > 0) {
            fieldName = objects[0];
        }
        if(fieldName.contains(".") && fieldName.indexOf(".") < fieldName.length()-1) {
            fieldName = fieldName.substring(fieldName.indexOf(".")+1);
        }
        String message = fieldName + "" + objectError.getDefaultMessage();
        pontifexResponse.getMeta().setCode(InnerBusinessException.INNER_BUSINESS_EXCEPTION_PARAM_ERROR.getCode()).setMessage(message);
        pontifexResponse.setResult(DefaultFailResponse.DEFAULT_FAIL_RESPONSE.getDefaultFailResponseVal());

        return pontifexResponse;

    }

    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public PontifexResponse validExceptionHandler(BindException bindException) {


        PontifexResponse pontifexResponse = new PontifexResponse();
        ObjectError objectError = bindException.getBindingResult().getAllErrors().get(0);
        String[] objects = objectError.getCodes();
        String fieldName = "";
        if(objects != null && objects.length > 0) {
            fieldName = objects[0];
        }
        if(fieldName.contains(".") && fieldName.indexOf(".") < fieldName.length()-1) {
            fieldName = fieldName.substring(fieldName.indexOf(".")+1);
        }
        String message = fieldName + "" + objectError.getDefaultMessage();
        pontifexResponse.getMeta().setCode(InnerBusinessException.INNER_BUSINESS_EXCEPTION_PARAM_ERROR.getCode()).setMessage(message);
        pontifexResponse.setResult(DefaultFailResponse.DEFAULT_FAIL_RESPONSE.getDefaultFailResponseVal());

        return pontifexResponse;

    }

    @ResponseBody
    @ExceptionHandler(value = PontifexRuntimeException.class)
    public PontifexResponse pontifexRuntimeExceptionHandler(PontifexRuntimeException exception) {
        
        PontifexResponse pontifexResponse = new PontifexResponse();
        pontifexResponse.getMeta().setCode(exception.errorCode).setMessage(exception.userMsg);
        pontifexResponse.setResult(DefaultFailResponse.DEFAULT_FAIL_RESPONSE.getDefaultFailResponseVal());

        return pontifexResponse;

    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object exceptionHandler(Exception exception) {

        return this.exceptionHandler.handler(exception);

    }

}
