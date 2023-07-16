package org.wyx.diego.pontifex.loader.handler;

import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.flow.FlowDeGradePipelineInterface;
import org.wyx.diego.pontifex.pipeline.PipelineInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class PipelineInterfaceInvocationHandler implements InvocationHandler {

    private PipelineInterfaceProxy pipelineInterfaceProxy;

    public PipelineInterfaceInvocationHandler(PipelineInterfaceProxy pipelineInterfaceProxy) {
        this.pipelineInterfaceProxy = pipelineInterfaceProxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if(!"call".equals(method.getName())) {
            return method.invoke(proxy, args);
        }
        PipelineInterface pipelineInterface = this.pipelineInterfaceProxy.getPipelineInterface();
        PontifexRequest<?> pontifexRequest = (PontifexRequest<?>) args[0];
        FlowDeGradePipelineInterface flowDeGradePipelineInterface = this.pipelineInterfaceProxy.getFlowDeGradePipelineInterface();
        PontifexResponse<?> pontifexResponse = flowDeGradePipelineInterface.invoke(pontifexRequest, (request) -> pipelineInterface.call(pontifexRequest), (request) -> pipelineInterface.fallback(pontifexRequest));

        return pontifexResponse;
    }

    public static class PipelineInterfaceProxy {

        private PipelineInterface pipelineInterface;

        private FlowDeGradePipelineInterface flowDeGradePipelineInterface;

        public PipelineInterface getPipelineInterface() {
            return pipelineInterface;
        }

        public PipelineInterfaceProxy setPipelineInterface(PipelineInterface pipelineInterface) {
            this.pipelineInterface = pipelineInterface;
            return this;
        }

        public FlowDeGradePipelineInterface getFlowDeGradePipelineInterface() {
            return flowDeGradePipelineInterface;
        }

        public PipelineInterfaceProxy setFlowDeGradePipelineInterface(FlowDeGradePipelineInterface flowDeGradePipelineInterface) {
            this.flowDeGradePipelineInterface = flowDeGradePipelineInterface;
            return this;
        }
    }

}
