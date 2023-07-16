package org.wyx.diego.pontifex.spring;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.wyx.diego.pontifex.alarm.biz.AlarmManager;
import org.wyx.diego.pontifex.alarm.biz.AlarmManagerInstance;
import org.wyx.diego.pontifex.alarm.biz.feishu.FeiShuSendMessage;
import org.wyx.diego.pontifex.alarm.config.AlarmConfig;
import org.wyx.diego.pontifex.cache.CacheManager;
import org.wyx.diego.pontifex.cache.CacheConfig;
import org.wyx.diego.pontifex.cache.CacheManagerInstance;
import org.wyx.diego.pontifex.context.PontifexContextInstance;
import org.wyx.diego.pontifex.spring.loader.SpringComponentLoader;
import org.wyx.diego.pontifex.spring.loader.SpringSequencePipelineLoader;
import org.wyx.diego.pontifex.spring.cache.DefaultCacheConfig;
import org.wyx.diego.pontifex.spring.exception.DefaultExcludePontifexExceptionHandler;
import org.wyx.diego.pontifex.spring.exception.ExcludePontifexExceptionHandler;
import org.wyx.diego.pontifex.spring.exception.GlobalDefaultExceptionHandler;
import org.wyx.diego.pontifex.spring.loader.SpringPipelineMataLoader;
import org.wyx.diego.pontifex.spring.manager.SpringPontifexManager;
import org.wyx.diego.pontifex.spring.request.MethodArgumentResolver;
import org.wyx.diego.pontifex.spring.request.PontifexParamHandler;

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
    public SpringPipelineMataLoader getSpringPipelineMataLoader() {
        return new SpringPipelineMataLoader();
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


    @Bean
    public CacheManager getCacheManager(@Autowired(required = false) CacheConfig cacheConfig, @Autowired(required = false) RedissonClient redissonClient) {

        CacheManagerInstance.INSTANCE.getCacheManager().setCacheConfig(cacheConfig).setRedissonClient(redissonClient);

        PontifexContextInstance.INSTANCE.getPontifexContext().getCacheContext().setCacheManager(CacheManagerInstance.INSTANCE.getCacheManager());
        CacheManagerInstance.INSTANCE.getCacheManager().init();

        return CacheManagerInstance.INSTANCE.getCacheManager();

    }

    @Bean
    public AlarmManager getAlarmManager() {
        return AlarmManagerInstance.INSTANCE.getAlarmManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public AlarmConfig getAlarmConfig() {
        AlarmConfig alarmConfig = new AlarmConfig();
        alarmConfig.setFeiShuUrl("https://open.feishu.cn/open-apis/bot/v2/hook/4c18778a-e7ea-431f-8611-6712baa9d835");
        return alarmConfig;
    }

    @Bean
    public FeiShuSendMessage getFeiShuSendMessage(AlarmConfig alarmConfig) {
        FeiShuSendMessage feiShuSendMessage;
        if(alarmConfig == null || alarmConfig.getFeiShuUrl() == null || "".equals(alarmConfig.getFeiShuUrl().trim())) {
            feiShuSendMessage = new FeiShuSendMessage(null);
            return feiShuSendMessage;
        }
        feiShuSendMessage = new FeiShuSendMessage(alarmConfig.getFeiShuUrl());
        return feiShuSendMessage;
    }

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;

    @Bean
    @ConditionalOnMissingBean
    public RedissonClient getRedisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+host+":"+port).setPassword(password);
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheConfig getCacheConfig() {

        CacheConfig config = new DefaultCacheConfig();
        return config;

    }

}

