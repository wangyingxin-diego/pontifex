package org.wyx.diego.pontifex.filter;

import org.wyx.diego.pontifex.*;
import org.wyx.diego.pontifex.annotation.Encryption;
import org.wyx.diego.pontifex.annotation.EncryptionDecryptionAlgorithm;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.util.AESBase64Util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.wyx.diego.pontifex.exception.ExceptionCode.EXCEPTION_CODE_PARAM_ENCRYPTION_ERROR;
import static org.wyx.diego.pontifex.exception.ExceptionCode.EXCEPTION_CODE_PARAM_ENCRYPTION_VALUE_ERROR;

public class EncryptionDecryptionFilter extends AbstractFilter {

    @Override
    public void doFilter(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse, FilterChain filterChain) {

    }

    @Override
    void before(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse) {



    }

    @Override
    void after(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse) {

        Object object = pontifexResponse.getResult();
        String secretKey = pontifexRequest.getSecretKey();
        if (object == null) {
        }
        if(object instanceof ListRes) {
            handleListRes((ListRes) object, secretKey);
            return;
        }

        if(object instanceof MapRes) {
            handleMapRes((MapRes) object, secretKey);
            return;
        }

        handleObject(object, secretKey);

    }

    private void handleMapRes(MapRes mapRes, String secretKey) {

        for(Object object : mapRes.values()) {
            handleObject(object, secretKey);
        }

    }

    private void handleListRes(ListRes listRes, String secretKey) {

        for(Object object : listRes) {
            handleObject(object, secretKey);
        }

    }

    private void handleObject(Object object, String secretKey) {

        if(object == null) {
            return;
        }

        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            // TODO
            return;
        }

        for (Field field : fields) {

            Encryption[] encryptions = field.getAnnotationsByType(Encryption.class);
            if (encryptions == null || encryptions.length == 0) {
                continue;
            }
            Class<?> cla = field.getType();
            if(cla == String.class) {
                handleString(field, clazz, encryptions[0], object, secretKey);
                continue;
            }

            if(EncryptionDecryptionLabel.class.isAssignableFrom(cla)) {
                handleEncryptionDecryptionLabel(field, clazz, encryptions[0], object, secretKey);
                continue;
            }
            if(cla == List.class) {
                handleList(object, field, secretKey);
            }


        }
    }

    private void handleList(Object object, Field field, String secretKey) {

        String fieldName = field.getName();

        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        List encryptionValue = null;
        try {
            Method method = object.getClass().getMethod(methodName);
            encryptionValue =  (List)method.invoke(object);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (ClassCastException e) {
            throw PontifexRuntimeException.exception(EXCEPTION_CODE_PARAM_ENCRYPTION_ERROR);
        }
        if(encryptionValue == null) {
            return;
        }
        for(Object o : encryptionValue) {
            handleObject(o, secretKey);
        }
    }

    private void handleEncryptionDecryptionLabel(Field field, Class clazz, Encryption encryption, Object object, String secretKey) {


        String fieldName = field.getName();

        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        EncryptionDecryptionLabel encryptionValue = null;
        try {
            Method method = clazz.getMethod(methodName);
            encryptionValue =  (EncryptionDecryptionLabel) method.invoke(object);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (ClassCastException e) {
            throw PontifexRuntimeException.exception(EXCEPTION_CODE_PARAM_ENCRYPTION_ERROR);
        }

        handleObject(encryptionValue, secretKey);

    }

    private void handleString(Field field, Class clazz, Encryption encryption, Object object, String secretKey) {

        String fieldName = field.getName();

        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String encryptionValue = null;
        try {
            Method method = clazz.getMethod(methodName);
            encryptionValue = (String) method.invoke(object);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (ClassCastException e) {
            throw PontifexRuntimeException.exception(EXCEPTION_CODE_PARAM_ENCRYPTION_ERROR);
        }
        if(encryptionValue == null) {
            throw PontifexRuntimeException.exception(EXCEPTION_CODE_PARAM_ENCRYPTION_VALUE_ERROR);
        }
        encryptionParam(field, encryption, encryptionValue, clazz, object, secretKey);

    }

    private void encryptionParam(Field field, Encryption encryption, String encryptionValue, Class<?> clazz, Object object, String secretKey) {

        String fieldName = field.getName();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        String decryptValue = encrypt(encryptionValue, encryption, secretKey);

        try {
            Method method = clazz.getDeclaredMethod(methodName, field.getType());
            method.invoke(object, decryptValue);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    private String encrypt(String encryptionValue, Encryption encryptionm, String key) {

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
                    result = AESBase64Util.encrypt(encryptionValue, secretKey);
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
}
