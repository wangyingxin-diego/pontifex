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
import org.wyx.diego.pontifex.annotation.EncryptionDecryptionAlgorithm;
import org.wyx.diego.pontifex.exception.ExceptionCode;
import org.wyx.diego.pontifex.BaseRequest;
import org.wyx.diego.pontifex.Request;
import org.wyx.diego.pontifex.annotation.Decryption;
import org.wyx.diego.pontifex.context.PontifexContextInstance;
import org.wyx.diego.pontifex.exception.InnerBusinessException;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.pipeline.PipelineInterface;
import org.wyx.diego.pontifex.util.AESBase64Util;
import org.wyx.diego.pontifex.PontifexRequest;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.stream.Collectors;

import static org.wyx.diego.pontifex.exception.InnerBusinessException.INNER_BUSINESS_EXCEPTION_HTTP_METHOD_NO_SUPPORT;

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

        if(!(httpMethod == HttpMethod.GET || httpMethod == HttpMethod.POST)) {
            LOGGER.error("pontifex not support httpMethod={}", httpMethod.name());
            PontifexRuntimeException.exception(INNER_BUSINESS_EXCEPTION_HTTP_METHOD_NO_SUPPORT);
        }

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

        String ip = getRemoteIp(webRequest);
        pontifexRequest.getRequestPayload().getWebRequest().setIp(ip);

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

    private String getRemoteIp(NativeWebRequest nativeWebRequest) {

        ServletWebRequest servletWebRequest = ((ServletWebRequest) nativeWebRequest);
        HttpServletRequest request = servletWebRequest.getRequest();

        String ip = null;
        // X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            // Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            // WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            // HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            // X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }

        // 有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        // 还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }

        //如果获取到的是127.0.0.1或0:0:0:0:0:0:0:1，就获取本地ip
        try {
            ip = getHostIP();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return ip;


    }

    private String getHostIP() throws SocketException {
        Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();

        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();

            // 去除回环接口，子接口，未运行接口
            if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                continue;
            }
            if (!netInterface.getDisplayName().contains("Intel") && !netInterface.getDisplayName().contains("Realtek") && !netInterface.getDisplayName().contains("eth0") && !netInterface.getDisplayName().contains("en0")) {
                continue;
            }

            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address) {
                    return ip.getHostAddress();
                }
            }
        }
        return null;
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
        PipelineInterface pipelineInterface = PontifexContextInstance.INSTANCE.getPontifexContext().getPipelineContext().getPipelineManager().getPipelineInterface(pontifexRequest.getBizKey());
        if(pipelineInterface != null && !pipelineInterface.isDecryptSwitch()) {
            return;
        }
        Type type = parameter.getParameter().getParameterizedType();
        Class<?> clazz = (Class<?>) ((ParameterizedTypeImpl) type).getActualTypeArguments()[0];
        String secretKey = getDecryptKey(pontifexRequest);

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

        List<Field> fields = getFieldList(clazz);
        if(fields == null || fields.size() == 0) {
            LOGGER.error("pontifex decrypt bizSign but fields is null, secretKey={}, bizSign={}", secretKey, bizSign);
            return;
        }

        String bizSignDec = null;
        try {
            bizSignDec = AESBase64Util.decrypt(bizSign, secretKey);
        } catch (Exception e) {
            LOGGER.error("pontifex decrypt bizSign error, secretKey={}, bizSign={}", secretKey, bizSign, e);
            throw PontifexRuntimeException.exception(ExceptionCode.EXCEPTION_CODE_PARAM_DECRYPT_ERROR);
        }
        JSONObject bizSignObject = JSONObject.parseObject(bizSignDec);
        Set<String> keySet = bizSignObject.keySet();

        for(String key : keySet) {
            Field field;
            field = getField(key.trim(), clazz);
            if(field == null) {
                String message = key + "不存在对应的属性";
                throw PontifexRuntimeException.exception(InnerBusinessException.INNER_BUSINESS_EXCEPTION_PARAM_ERROR.getCode(), message);
            }
            String decryptValue = (String)bizSignObject.get(key);
            setBizSignValue(field, baseRequest, decryptValue, clazz);
        }

    }

    private String getDecryptKey(PontifexRequest pontifexRequest) {

        PipelineInterface pipelineInterface = PontifexContextInstance.INSTANCE.getPontifexContext().getPipelineContext().getPipelineManager().getPipelineInterface(pontifexRequest.getBizKey());
        String decryptKey = null;
        if(pipelineInterface != null) {
            decryptKey = pipelineInterface.getDecryptKey();
        }
        if(decryptKey != null && "".equals(decryptKey.trim())) {
            return decryptKey;
        }
        decryptKey = pontifexRequest.getDecryptKey();
        if(decryptKey == null || "".equals(decryptKey.trim())) {
            LOGGER.error("pontifex decrypt decryptKey=null");
            throw PontifexRuntimeException.exception(ExceptionCode.EXCEPTION_CODE_PARAM_DECRYPT_ERROR);
        }
        return decryptKey;

    }

    private Field getField(String key, Class clazz) {

        Field field = null;

        while (clazz != Object.class) {

            try {
                field = clazz.getDeclaredField(key.trim());
                return field;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }

        return field;

    }

    private List<Field> getFieldList(Class clazz) {

        List<Field> fieldList = new ArrayList<>();
        while (clazz != Object.class) {
            fieldList.addAll(Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toList()));
            clazz = clazz.getSuperclass();
        }

        return fieldList;

    }


    private Method getMethod(String methodName, Class clazz, Class<?> paramClass) {

        Method method = null;
        while (clazz != Object.class) {

            try {
                if(paramClass == null) {
                    method = clazz.getDeclaredMethod(methodName.trim());
                } else {
                    method = clazz.getDeclaredMethod(methodName.trim(), paramClass);
                }
                return method;
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }

        return null;

    }

    private void setBizSignValue(Field field, Request request, String decryptValue, Class<?> clazz) {

        String fieldName = field.getName();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        try {
            Method method = getMethod(methodName.trim(), clazz, field.getType());
            if(method == null) {
                String message = fieldName + "不存在对应的方法";
                throw PontifexRuntimeException.exception(InnerBusinessException.INNER_BUSINESS_EXCEPTION_PARAM_ERROR.getCode(), message);
            }
            method.invoke(request, decryptValue);
        } catch (IllegalAccessException e) {
            LOGGER.error("pontifex decrypt set value error, fieldName={}, decryptValue={}", fieldName, decryptValue);
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            LOGGER.error("pontifex decrypt set value error, fieldName={}, decryptValue={}", fieldName, decryptValue);
            throw new RuntimeException(e);
        }

    }


    private void handleParamDecrypt(Class<?> clazz, Request request, String secretKey) {

        List<Field> fields = getFieldList(clazz);
        if(fields == null || fields.size() == 0) {
            // TODO
            LOGGER.error("pontifex decrypt bizSign but fields is null, secretKey={}, class={}", secretKey, clazz);
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

                Method method = getMethod(methodName, clazz, null);
                encryptionValue = (String) method.invoke(request);
            } catch (IllegalAccessException e) {
                LOGGER.error("pontifex decrypt set value error, fieldName={}, request={}", fieldName, JSONObject.toJSONString(request));
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                LOGGER.error("pontifex decrypt set value error, fieldName={}, request={}", fieldName, JSONObject.toJSONString(request));
                throw new RuntimeException(e);
            }
            String key = encryption.key();
            if(key != null || "".equals(key.trim())) {
                secretKey = key;
            }
            decryptParam(field, encryption, encryptionValue, clazz, request, secretKey);

        }

    }

    private void decryptParam(Field field, Decryption encryption, String encryptionValue, Class<?> clazz, Request request, String secretKey) {

        String fieldName = field.getName();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        String decryptValue = decrypt(encryptionValue, encryption, secretKey);

        try {
            Method method = getMethod(methodName, clazz, field.getType());
            method.invoke(request, decryptValue);
        } catch (IllegalAccessException e) {
            LOGGER.error("pontifex encryption set value error, fieldName={}, decryptValue={}", fieldName, decryptValue);
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            LOGGER.error("pontifex encryption set value error, fieldName={}, decryptValue={}", fieldName, decryptValue);
            throw new RuntimeException(e);
        }

    }

    private String decrypt(String decryptionValue, Decryption decryption, String key) {

        EncryptionDecryptionAlgorithm encryptionAlgorithm = decryption.algorithm();
        String innerKey = decryption.key();
        String secretKey = null;
        if(innerKey != null && !"".equals(innerKey.trim())) {
            secretKey = innerKey.trim();
        }
        if(secretKey == null && key != null && !"".equals(key.trim())) {
            secretKey = key;
        }
        if(secretKey == null) {
            LOGGER.error("pontifex decrypt secretKey is null, key={}, encryptionValue={}", key, decryptionValue);
            throw PontifexRuntimeException.exception(ExceptionCode.EXCEPTION_CODE_PARAM_DECRYPT_ERROR);
        }

        String result = null;
        switch (encryptionAlgorithm) {

            case AES128: {
                try {
                    result = AESBase64Util.decrypt(decryptionValue, secretKey);
                } catch (Exception e) {
                    LOGGER.error("pontifex decrypt secretKey is null, key={}, encryptionValue={}", key, decryptionValue);
                    throw PontifexRuntimeException.exception(ExceptionCode.EXCEPTION_CODE_PARAM_DECRYPT_ERROR);
                }
                break;
            }
            default: {
                LOGGER.error("pontifex decrypt encryptionAlgorithm not support, encryptionValue={}", key, decryptionValue);
                throw PontifexRuntimeException.exception(ExceptionCode.EXCEPTION_CODE_PARAM_ALGORITHM_ERROR);
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
