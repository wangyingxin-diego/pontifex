package org.wyx.diego.pontifex.spring.request;

import com.alibaba.fastjson.JSONObject;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.wyx.diego.pontifex.BaseRequest;
import org.wyx.diego.pontifex.Request;
import org.wyx.diego.pontifex.annotation.Decryption;
import org.wyx.diego.pontifex.annotation.EncryptionDecryptionAlgorithm;
import org.wyx.diego.pontifex.exception.InnerBusinessException;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.util.AESBase64Util;
import org.wyx.diego.pontifex.PontifexRequest;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PontifexHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {


    private static final Logger LOGGER = LoggerFactory.getLogger(PontifexHandlerMethodArgumentResolver.class);


    private RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor;
    private PontifexParamHandler pontifexParamHandler;

    private static final ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory();

    public PontifexHandlerMethodArgumentResolver(RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor, PontifexParamHandler pontifexParamHandler) {
        this.requestResponseBodyMethodProcessor = requestResponseBodyMethodProcessor;
        if(pontifexParamHandler == null) {
            this.pontifexParamHandler = new DefaultPontifexParamHandler();
        } else {
            this.pontifexParamHandler = pontifexParamHandler;
        }
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        if(parameter.getParameterType() == PontifexRequest.class) {
            return true;
        }

        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        try {
            return resolveArgument0(parameter, mavContainer, webRequest, binderFactory);
        } catch (Exception e) {
            if(e instanceof MethodArgumentNotValidException) {
                throw  e;
            }
            if(e instanceof PontifexRuntimeException) {
                throw e;
            }
            LOGGER.error("pontifex resolveArgument error", e);
            throw PontifexRuntimeException.exception(InnerBusinessException.INNER_BUSINESS_EXCEPTION_PARAM_ERROR);
        }

    }

    private PontifexRequest resolveArgument0(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception{

        LOGGER.info("pontifex resolveArgument0 parameter={}", JSONObject.toJSONString(parameter));
        ServletWebRequest servletWebRequest = ((ServletWebRequest) webRequest);
        HttpMethod httpMethod = servletWebRequest.getHttpMethod();
        PontifexRequest pontifexRequest;

        if(httpMethod == HttpMethod.GET) {
            pontifexRequest = handleGet(parameter, webRequest);
        } else {
            pontifexRequest =  (PontifexRequest) requestResponseBodyMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        }

        LOGGER.info("pontifex argument resolver pontifexRequest={}", JSONObject.toJSONString(pontifexRequest));

        Iterator<String> iterator = webRequest.getHeaderNames();
        while (iterator.hasNext()) {
            String name = iterator.next();
            pontifexRequest.getHeaders().put(name, webRequest.getHeader(name));
        }

        PontifexParam pontifexParam = new PontifexParam(pontifexRequest, servletWebRequest);
        try {
            this.pontifexParamHandler.handle(pontifexParam);
        } catch (Throwable e) {
            throwPontifexRuntimeException(e);
        }

        LOGGER.info("pontifex argument resolver pontifexRequest={}", JSONObject.toJSONString(pontifexRequest));

        try {
            handleParam(pontifexRequest, parameter);
        } catch (Exception e) {
            throwPontifexRuntimeException(e);
        }

        LOGGER.info("pontifex argument resolver pontifexRequest={}", JSONObject.toJSONString(pontifexRequest));

        return pontifexRequest;
    }

    private void throwPontifexRuntimeException(Throwable throwable) {

        if(throwable instanceof PontifexRuntimeException) {
            throw (PontifexRuntimeException)throwable;
        }
        throw PontifexRuntimeException.exception(InnerBusinessException.INNER_BUSINESS_EXCEPTION_DECRYPT_ERROR);
    }

    private void handleParam(PontifexRequest pontifexRequest, MethodParameter parameter) {

        if(!pontifexRequest.isDecryptSwitch()) {
            return;
        }
        Type type = parameter.getParameter().getParameterizedType();
        Class<?> clazz = (Class<?>) ((ParameterizedTypeImpl) type).getActualTypeArguments()[0];
        String secretKey = pontifexRequest.getSecretKey();

        Request request = pontifexRequest.getBizObject();
        String bizSign = null;
        if(request instanceof BaseRequest) {
            bizSign = ((BaseRequest) request).getBizSign();
        }
        if(bizSign != null && !"".equals(bizSign.trim())) {
            BaseRequest baseRequest = (BaseRequest) request;
            bizSign = baseRequest.getBizSign();
            handleBizSign(bizSign, clazz, baseRequest, secretKey);
        } else {
            handleParamDecrypt(clazz, request, secretKey);
        }

    }

    private void handleBizSign(String bizSign, Class<?> clazz, BaseRequest baseRequest, String secretKey) {

        Field[] fields = clazz.getDeclaredFields();
        if(fields == null || fields.length == 0) {
            // TODO
            return;
        }

        String bizSignDec = null;
        try {
            bizSignDec = AESBase64Util.decrypt(bizSign, secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject bizSignObject = JSONObject.parseObject(bizSignDec);
        Set<String> keySet = bizSignObject.keySet();

        for(String key : keySet) {
            Field field;
            try {
                field = clazz.getDeclaredField(key.trim());
            } catch (NoSuchFieldException e) {
                String message = key + "不存在对应的属性";
                throw PontifexRuntimeException.exception(InnerBusinessException.INNER_BUSINESS_EXCEPTION_PARAM_ERROR.getCode(), message);
            }
            String decryptValue = (String)bizSignObject.get(key);
            setBizSignValue(field, baseRequest, decryptValue, clazz);
        }

    }

    private void setBizSignValue(Field field, Request request, String decryptValue, Class<?> clazz) {

        String fieldName = field.getName();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        try {
            Method method = clazz.getDeclaredMethod(methodName, field.getType());
            method.invoke(request, decryptValue);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }


    private void handleParamDecrypt(Class<?> clazz, Request request, String secretKey) {

        Field[] fields = clazz.getDeclaredFields();
        if(fields == null || fields.length == 0) {
            // TODO
            return;
        }

        for (Field field : fields) {

            Decryption[] encryptions = field.getAnnotationsByType(Decryption.class);
            if(encryptions == null || encryptions.length == 0) {
                continue;
            }
            Decryption encryption = encryptions[0];
            String name = encryption.name();
            String fieldName = field.getName();
            if(name == null || "".equals(name.trim())) {
                name = fieldName;
            }

            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            String encryptionValue = null;
            try {
                Method method = clazz.getMethod(methodName);
                encryptionValue = (String) method.invoke(request);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            decryptParam(field, encryption, encryptionValue, clazz, request, secretKey);

        }

    }

    private void decryptParam(Field field, Decryption encryption, String encryptionValue, Class<?> clazz, Request request, String secretKey) {

        String fieldName = field.getName();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        String decryptValue = decrypt(encryptionValue, encryption, secretKey);

        try {
            Method method = clazz.getDeclaredMethod(methodName, field.getType());
            method.invoke(request, decryptValue);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    private String decrypt(String encryptionValue, Decryption encryptionm, String key) {

        EncryptionDecryptionAlgorithm encryptionAlgorithm = encryptionm.algorithm();
        String innerKey = encryptionm.key();
        String secretKey = null;
        if(innerKey != null && !"".equals(innerKey.trim())) {
            secretKey = innerKey.trim();
        }
        if(secretKey == null && key != null && !"".equals(key.trim())) {
            secretKey = key;
        }
        if(secretKey == null) {
            throw new RuntimeException();
        }

        String result = null;
        switch (encryptionAlgorithm) {

            case AES128: {
                try {
                    result = AESBase64Util.decrypt(encryptionValue, secretKey);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            default: {
                throw new RuntimeException();
            }

        }

        return result;

    }

    private PontifexRequest handleGet(MethodParameter parameter, NativeWebRequest webRequest) {

        Type type = parameter.getParameter().getParameterizedType();
        Class<?> clazz = (Class<?>) ((ParameterizedTypeImpl) type).getActualTypeArguments()[0];
        JSONObject jsonObject = new JSONObject();
        PontifexRequest pontifexRequest = new PontifexRequest();

        Map<String, String[]> stringStringMap = webRequest.getParameterMap();

        String bizKey = null;
        Iterator<Map.Entry<String, String[]>> entryIterator = stringStringMap.entrySet().iterator();
        while (entryIterator.hasNext()) {

            Map.Entry<String, String[]> entry = entryIterator.next();
            String key = entry.getKey();
            if("bizKey".equals(key.trim())) {
                bizKey = entry.getValue()[0];
                continue;
            }
            jsonObject.put(key, entry.getValue()[0]);

        }
        Object bizObject = jsonObject.toJavaObject(clazz);
        pontifexRequest.setBizObject((Request) bizObject).setBizKey(bizKey);

        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Object>> violationSet = validator.validate(bizObject);
        Iterator<ConstraintViolation<Object>> iterator = violationSet.iterator();

        while (iterator.hasNext()) {
            ConstraintViolation<Object> constraintViolation = iterator.next();
            String message = constraintViolation.getPropertyPath().toString() + constraintViolation.getMessage();
            throw PontifexRuntimeException.exception(InnerBusinessException.INNER_BUSINESS_EXCEPTION_PARAM_ERROR.getCode(), message);
        }

        return pontifexRequest;

    }
}
