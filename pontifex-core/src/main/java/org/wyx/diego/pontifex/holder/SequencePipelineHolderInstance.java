package org.wyx.diego.pontifex.holder;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.Holder;
import org.wyx.diego.pontifex.ModuleType;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.annotation.RuntimeMeta;
import org.wyx.diego.pontifex.annotation.TaskMeta;
import org.wyx.diego.pontifex.bytecode.NebulaJavassistProxy;
import org.wyx.diego.pontifex.exception.ExceptionCode;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;
import org.wyx.diego.pontifex.loader.handler.TaskInvocationHandler;
import org.wyx.diego.pontifex.loader.runtime.RuntimeObject;
import org.wyx.diego.pontifex.loader.runtime.TaskRuntimeObject;
import org.wyx.diego.pontifex.pipeline.*;

import static org.wyx.diego.pontifex.exception.ExceptionCode.EXCEPTION_CODE_PL_TYPE;

/**
 * @author wangyingxin
 * @title: PipelineHolderInstance
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/30
 */
public enum SequencePipelineHolderInstance implements PipelineHolder {

    INSTANCE(new SequencePipelineHolder());

    private SequencePipelineHolder pipelineHolder;

    SequencePipelineHolderInstance(SequencePipelineHolder pipelineHolder) {
        this.pipelineHolder = pipelineHolder;
    }

    @Override
    public Pipeline obtain(PontifexRequest pontifexRequest) {
        return this.pipelineHolder.obtain(pontifexRequest);
    }

    @Override
    public Task<?, ?, ?> load(Task<?, ?, ?> task) {
        return this.pipelineHolder.load(task);
    }

    private static class SequencePipelineHolder extends AbstractPipelineHolder {

        private static final Logger logger = LoggerFactory.getLogger(SequencePipelineHolder.class);

        @Override
        public Pipeline obtain(PontifexRequest pontifexRequest) {
            return pipelines.get(pontifexRequest.getBizKey());
        }

        @Override
        public Task<?, ?, ?> load(Task<?, ?, ?> task) {

            TaskMeta[] taskMetas = task.getClass().getAnnotationsByType(TaskMeta.class);

            if(taskMetas == null) {
                logger.warn("DefaultPontifexManager, task have not TaskMeta!, task={}", task.getClass().getName());
                throw PontifexRuntimeException.exception(ExceptionCode.EXCEPTION_CODE_TASK_META);
            }

            for(TaskMeta taskMeta : taskMetas) {

                TaskRuntimeObject runtimeObject = new TaskRuntimeObject(taskMeta, task);
                TaskInvocationHandler taskInvocationHandler = new TaskInvocationHandler(task, runtimeObject);
                PLTask nebulaJavassistProxy = (PLTask) NebulaJavassistProxy.getProxy(task.getClass()).newInstance(taskInvocationHandler);
                TaskParam taskParam = new TaskParam();
                taskParam.setTask(nebulaJavassistProxy).setTaskMeta(taskMeta);
                load0(taskParam);

            }

            return task;

        }
        public PLTask load0(TaskParam taskParam) {

            PLTask task = taskParam.getTask();
            TaskMeta taskMeta = taskParam.getTaskMeta();
            try {

                logger.error("DefaultPontifexManager, task={}", JSONObject.toJSONString(taskMeta));

                String pipelineName = taskMeta.pipelineName();

                logger.error("DefaultPontifexManager, pipelineName={}", pipelineName);
                if("".equals(pipelineName)) return null;
                ProxyedTask proxyedTask = new ProxyedTask(task);
                int sort = taskMeta.sort();
                proxyedTask.setSort(sort);
                proxyedTask.setTaskType(TaskType.SEQUENCE);
                Pipeline pipeline =new SequencePipeline();
                Pipeline pipelined = pipelines.putIfAbsent(pipelineName, pipeline);
                if(pipelined != null && !(pipelined instanceof SequencePipeline)) {
                    throw PontifexRuntimeException.exception(EXCEPTION_CODE_PL_TYPE);
                }
                pipelines.get(pipelineName).addTask(proxyedTask);
                System.out.println("DefaultPontifexManager:" + pipelines.toString());
                return proxyedTask;

            } catch (Exception e) {
                throw new PontifexRuntimeException(111, "", e);
            }
        }

        private static class TaskParam {

            private PLTask task;

            private TaskMeta taskMeta;

            private RuntimeMeta defaultRuntimeMeta;

            public PLTask getTask() {
                return task;
            }

            public TaskParam setTask(PLTask task) {
                this.task = task;
                return this;
            }

            public TaskMeta getTaskMeta() {
                return taskMeta;
            }

            public TaskParam setTaskMeta(TaskMeta taskMeta) {
                this.taskMeta = taskMeta;
                return this;
            }

            public RuntimeMeta getDefaultRuntimeMeta() {
                return defaultRuntimeMeta;
            }

            public TaskParam setDefaultRuntimeMeta(RuntimeMeta defaultRuntimeMeta) {
                this.defaultRuntimeMeta = defaultRuntimeMeta;
                return this;
            }
        }

    }
}
