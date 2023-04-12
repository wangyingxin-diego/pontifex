package org.wyx.diego.pontifex.spring.request;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MethodArgumentResolver {

//    @Resource
    private RequestMappingHandlerAdapter adapter;
//    @Autowired(required = false)
    private PontifexParamHandler pontifexParamHandler;

    public MethodArgumentResolver(RequestMappingHandlerAdapter adapter, PontifexParamHandler pontifexParamHandler) {
        this.adapter = adapter;
        if(pontifexParamHandler == null) {
            this.pontifexParamHandler = new DefaultPontifexParamHandler();
        } else {
            this.pontifexParamHandler = pontifexParamHandler;
        }
    }

    @PostConstruct
    public void injectSelfMethodArgumentResolver() {

        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
        List<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers = adapter.getArgumentResolvers();
        for (HandlerMethodArgumentResolver handlerMethodArgumentResolver : handlerMethodArgumentResolvers) {
            if(handlerMethodArgumentResolver instanceof RequestResponseBodyMethodProcessor) {
                argumentResolvers.add(new PontifexHandlerMethodArgumentResolver((RequestResponseBodyMethodProcessor)handlerMethodArgumentResolver, pontifexParamHandler));
            }
        }

        argumentResolvers.addAll(adapter.getArgumentResolvers());
        adapter.setArgumentResolvers(argumentResolvers);

    }

}
