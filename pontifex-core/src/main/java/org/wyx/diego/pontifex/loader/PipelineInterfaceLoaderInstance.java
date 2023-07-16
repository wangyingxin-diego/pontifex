package org.wyx.diego.pontifex.loader;

import org.wyx.diego.pontifex.Loader;
import org.wyx.diego.pontifex.annotation.PipelineMeta;
import org.wyx.diego.pontifex.bytecode.NebulaJavassistProxy;
import org.wyx.diego.pontifex.flow.FlowDeGradePipelineInterface;
import org.wyx.diego.pontifex.flow.annotation.DeGrade;
import org.wyx.diego.pontifex.flow.annotation.Flow;
import org.wyx.diego.pontifex.flow.annotation.FlowDeGrade;
import org.wyx.diego.pontifex.loader.handler.PipelineInterfaceInvocationHandler;
import org.wyx.diego.pontifex.pipeline.PipelineInterface;

public enum PipelineInterfaceLoaderInstance implements Loader<PipelineInterfaceLoaderInstance.LoadParam, PipelineInterface> {


    INSTANCE(new BasePipelineInterfaceLoader());
    private BasePipelineInterfaceLoader basePipelineInterfaceLoader;

    PipelineInterfaceLoaderInstance(BasePipelineInterfaceLoader basePipelineInterfaceLoader) {
        this.basePipelineInterfaceLoader = basePipelineInterfaceLoader;
    }

    private static class BasePipelineInterfaceLoader implements Loader<LoadParam, PipelineInterface> {

        @Override
        public PipelineInterface load(LoadParam loadParam) {


            PipelineMeta pipelineMeta = loadParam.pipelineMeta;
            FlowDeGrade flowDeGrade = pipelineMeta.flowDeGrade();

            FlowDeGradePipelineInterface flowDeGradePipelineInterface = loadParam.getFlowDeGradePipelineInterface();
            flowDeGradePipelineInterface.init(pipelineMeta.name(), flowDeGrade);

            PipelineInterfaceInvocationHandler.PipelineInterfaceProxy pipelineInterfaceProxy = new PipelineInterfaceInvocationHandler.PipelineInterfaceProxy();
            pipelineInterfaceProxy.setPipelineInterface(loadParam.getPipelineInterface()).setFlowDeGradePipelineInterface(flowDeGradePipelineInterface);
            PipelineInterfaceInvocationHandler pipelineInterfaceInvocationHandler = new PipelineInterfaceInvocationHandler(pipelineInterfaceProxy);
            PipelineInterface pipelineInterface = (PipelineInterface) NebulaJavassistProxy.getProxy(loadParam.getPipelineInterface().getClass()).newInstance(pipelineInterfaceInvocationHandler);

            return pipelineInterface;
        }

    }

    @Override
    public PipelineInterface load(LoadParam loadParam) {
        PipelineInterface pipelineInterface = this.basePipelineInterfaceLoader.load(loadParam);
        return pipelineInterface;
    }

    public static class LoadParam {

        private PipelineInterface pipelineInterface;
        private PipelineMeta pipelineMeta;

        private FlowDeGradePipelineInterface flowDeGradePipelineInterface;

        public PipelineInterface getPipelineInterface() {
            return pipelineInterface;
        }

        public LoadParam setPipelineInterface(PipelineInterface pipelineInterface) {
            this.pipelineInterface = pipelineInterface;
            return this;
        }

        public PipelineMeta getPipelineMeta() {
            return pipelineMeta;
        }

        public LoadParam setPipelineMeta(PipelineMeta pipelineMeta) {
            this.pipelineMeta = pipelineMeta;
            return this;
        }

        public FlowDeGradePipelineInterface getFlowDeGradePipelineInterface() {
            return flowDeGradePipelineInterface;
        }

        public LoadParam setFlowDeGradePipelineInterface(FlowDeGradePipelineInterface flowDeGradePipelineInterface) {
            this.flowDeGradePipelineInterface = flowDeGradePipelineInterface;
            return this;
        }
    }


}
