//package org.wyx.diego.pontifex.demo;
//
//import com.alibaba.fastjson.JSONObject;
//import org.springframework.core.MethodParameter;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//import org.wyx.diego.pontifex.PontifexRequest;
//import org.wyx.diego.pontifex.Request;
//import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
//
//import java.beans.PropertyDescriptor;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.lang.reflect.Type;
//import java.util.Iterator;
//import java.util.Map;
//
//public class Resss implements HandlerMethodArgumentResolver {
//
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//
//        if(parameter.getParameterType() == PontifexRequest.class) {
//            return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//
//        Type type = parameter.getParameter().getParameterizedType();
//        Class<?> clazz = (Class<?>) ((ParameterizedTypeImpl) type).getActualTypeArguments()[0];
//
//        Map<String, String[]> stringStringMap = webRequest.getParameterMap();
//
//
//
////        Object object = clazz.newInstance();
////        Iterator<Map.Entry<String, String[]>> iterator = stringStringMap.entrySet().iterator();
////        String bizKey = null;
////        while (iterator.hasNext()) {
////            Map.Entry<String, String[]> entry = iterator.next();
////            String key = entry.getKey();
////            if("bizKey".equals(key.trim())) {
////                bizKey = entry.getValue()[0];
////                continue;
////            }
////            PropertyDescriptor propDesc = new PropertyDescriptor(key, clazz);
////            Method method = propDesc.getWriteMethod();
////            method.invoke(object, entry.getValue()[0]);
////        }
//
//        String bizKey = null;
//        Iterator<Map.Entry<String, String[]>> entryIterator = stringStringMap.entrySet().iterator();
//        JSONObject jsonObject = new JSONObject();
//        while (entryIterator.hasNext()) {
//
//            Map.Entry<String, String[]> entry = entryIterator.next();
//            String key = entry.getKey();
//            if("bizKey".equals(key.trim())) {
//                bizKey = entry.getValue()[0];
//                continue;
//            }
//            jsonObject.put(key, entry.getValue()[0]);
//
//        }
//        Object bizObject = jsonObject.toJavaObject(clazz);
//
//        PontifexRequest pontifexRequest = new PontifexRequest();
//        pontifexRequest.setBizObject((Request) bizObject).setBizKey(bizKey);
//
//        return pontifexRequest;
//
//    }
//}
