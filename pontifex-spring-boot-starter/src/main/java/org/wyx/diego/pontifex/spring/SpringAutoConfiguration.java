package org.wyx.diego.pontifex.spring;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.support.springfox.SwaggerJsonSerializer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.wyx.diego.pontifex.spring.exception.DefaultExcludePontifexExceptionHandler;
import org.wyx.diego.pontifex.spring.exception.ExcludePontifexExceptionHandler;
import org.wyx.diego.pontifex.spring.exception.GlobalDefaultExceptionHandler;
import org.wyx.diego.pontifex.spring.loader.SpringComponentLoader;
import org.wyx.diego.pontifex.spring.loader.SpringSequencePipelineLoader;
import org.wyx.diego.pontifex.spring.manager.SpringPontifexManager;
import org.wyx.diego.pontifex.spring.request.MethodArgumentResolver;
import org.wyx.diego.pontifex.spring.request.PontifexParamHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangyingxin
 * @title: SpringAutoConfiguration
 * @projectName pontifex
 * @description: TODO
 * @date 2021/12/29
 */
@Configuration
public class SpringAutoConfiguration {

    @Bean
    public SpringComponentLoader getSpringComponentLoader() {
        return new SpringComponentLoader();
    }

    @Bean
    public SpringSequencePipelineLoader getSpringSequencePipelineLoader() {
        return new SpringSequencePipelineLoader();
    }

    @Bean
    public SpringPontifexManager getSpringPontifexManager() {
        return new SpringPontifexManager();
    }

    @Bean
    public MethodArgumentResolver getMethodArgumentResolver(RequestMappingHandlerAdapter adapter, @Autowired(required = false) PontifexParamHandler pontifexParamHandler) {
        return new MethodArgumentResolver(adapter, pontifexParamHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExcludePontifexExceptionHandler getExcludePontifexExceptionHandler() {
        ExcludePontifexExceptionHandler exceptionHandler = new DefaultExcludePontifexExceptionHandler();
        return exceptionHandler;
    }

    @Bean
    public GlobalDefaultExceptionHandler getGlobalDefaultExceptionHandler(ExcludePontifexExceptionHandler exceptionHandler) {
        return new GlobalDefaultExceptionHandler(exceptionHandler);
    }

//    @Bean
//    public Encoder feignEncoder() {
//        return new SpringEncoder(feignHttpMessageConverter());
//    }
//
//    @Bean
//    public Decoder feignDecoder() {
//        return new SpringDecoder(feignHttpMessageConverter());
//    }
//
//    /**
//     * 设置解码器为fastjsonfeign
//     *
//     * @return
//     */
//    private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
//        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(this.getFastJsonConverter());
//        return () -> httpMessageConverters;
//    }
//
//    private FastJsonHttpMessageConverter getFastJsonConverter() {
//
//        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
//
//        List<MediaType> supportedMediaTypes = new ArrayList<>();
//        MediaType mediaTypeJson = MediaType.valueOf(MediaType.APPLICATION_JSON_UTF8_VALUE);
//        supportedMediaTypes.add(mediaTypeJson);
//        converter.setSupportedMediaTypes(supportedMediaTypes);
//        FastJsonConfig config = new FastJsonConfig();
////        config.getSerializeConfig().put(JSON.class, new SwaggerJsonSerializer());
//        config.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
//        converter.setFastJsonConfig(config);
//
//        return converter;
//    }
}

