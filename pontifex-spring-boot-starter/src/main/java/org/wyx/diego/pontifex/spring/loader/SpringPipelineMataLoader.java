package org.wyx.diego.pontifex.spring.loader;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.wyx.diego.pontifex.annotation.PipelineMeta;
import org.wyx.diego.pontifex.annotation.TaskMeta;
import org.wyx.diego.pontifex.bytecode.ReflectUtils;
import org.wyx.diego.pontifex.cache.CacheManager;
import org.wyx.diego.pontifex.cache.CacheInterface;
import org.wyx.diego.pontifex.cache.GetKey;
import org.wyx.diego.pontifex.context.PontifexContextInstance;
import org.wyx.diego.pontifex.flow.FlowDeGradePipelineInterface;
import org.wyx.diego.pontifex.flow.config.FlowConfig;
import org.wyx.diego.pontifex.loader.PipelineInterfaceLoaderInstance;
import org.wyx.diego.pontifex.pipeline.PipelineInterface;
import org.wyx.diego.pontifex.spring.annotation.PipelineMateSpring;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SpringPipelineMataLoader implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        TaskMeta taskMeta = bean.getClass().getAnnotation(TaskMeta.class);
        Controller controller = bean.getClass().getAnnotation(Controller.class);
        RestController restController = bean.getClass().getAnnotation(RestController.class);
        PipelineMateSpring pipelineMateSpring = bean.getClass().getAnnotation(PipelineMateSpring.class);

        if(taskMeta == null && controller == null && restController == null && pipelineMateSpring == null) {
            return bean;
        }

        List<PipelineMeta> pipelineMetas = getPipelineMetas(bean);
        if(pipelineMetas.size() == 0) {
            return bean;
        }

        GetKey<?> getKey = null;
        if(bean instanceof CacheInterface) {
            CacheInterface<?> cacheInterface = (CacheInterface<?>) bean;
            getKey = cacheInterface.getKey();
        }

        for(PipelineMeta pipelineMeta : pipelineMetas) {
            CacheManager.addPipeline(pipelineMeta, getKey);
        }

        Object object = handlePipeline(bean, pipelineMateSpring);

        if(object != null) {
            return object;
        }

        return bean;
    }

    private Object handlePipeline(Object bean, PipelineMateSpring pipelineMateSpring) {

        if(!(bean instanceof PipelineInterface)) {
            return bean;
        }

        String pipelineName = pipelineMateSpring.pipelineMeta().name();

        PipelineInterface pipelineInterface = (PipelineInterface) bean;

        PontifexContextInstance.INSTANCE.getPontifexContext().getPipelineContext().getPipelineManager().addPipelineInterface(pipelineName, pipelineInterface);

        Object object = handleLoad(bean, pipelineMateSpring);

        return object;

    }

    private List<PipelineMeta> getPipelineMetas(Object bean) {

        List<PipelineMeta> pipelineMetas = new ArrayList<>();
        PipelineMateSpring pipelineMateSpring = bean.getClass().getAnnotation(PipelineMateSpring.class);
        if(pipelineMateSpring != null) {
            pipelineMetas.add(pipelineMateSpring.pipelineMeta());
            return pipelineMetas;
        }

        PipelineMeta pipelineMeta = bean.getClass().getAnnotation(PipelineMeta.class);
        if(pipelineMeta != null) {
            pipelineMetas.add(pipelineMeta);
        }
        List<Method> methodList = ReflectUtils.getMethods(bean.getClass());
        for(Method method : methodList) {
            pipelineMeta = method.getAnnotation(PipelineMeta.class);
            if(pipelineMeta == null) {
                continue;
            }
            pipelineMetas.add(pipelineMeta);
        }
        return pipelineMetas;

    }

    private Object handleLoad(Object bean, PipelineMateSpring pipelineMateSpring) {

        if(!(bean instanceof PipelineInterface)) {
            return bean;
        }
        FlowConfig flowConfig = null;
        try {
            flowConfig = this.applicationContext.getBean(FlowConfig.class);
        } catch (BeansException e) {
            return bean;
        } finally {
        }
        if(flowConfig == null) {
            return bean;
        }
        Class flowClass = flowConfig.flowClass();
        if(flowClass == null) {
            return bean;
        }
        FlowDeGradePipelineInterface flowDeGradePipelineInterface = null;
        try {
            flowDeGradePipelineInterface = (FlowDeGradePipelineInterface)flowClass.newInstance();
        } catch (InstantiationException e) {
            return bean;
        } catch (IllegalAccessException e) {
            return bean;
        }

        PipelineInterface pipelineInterface = (PipelineInterface) bean;
        PipelineMeta pipelineMeta = pipelineMateSpring.pipelineMeta();
        PipelineInterfaceLoaderInstance.LoadParam loadParam = new PipelineInterfaceLoaderInstance.LoadParam();
        loadParam.setPipelineMeta(pipelineMeta).setPipelineInterface(pipelineInterface).setFlowDeGradePipelineInterface(flowDeGradePipelineInterface);
        PipelineInterface pipelineInterface1 = PipelineInterfaceLoaderInstance.INSTANCE.load(loadParam);

        return pipelineInterface1;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
