package org.wyx.diego.pontifex.loader;

import org.wyx.diego.pontifex.Component;
import org.wyx.diego.pontifex.Loader;
import org.wyx.diego.pontifex.ModuleType;
import org.wyx.diego.pontifex.annotation.ComponentMeta;
import org.wyx.diego.pontifex.annotation.RuntimeMeta;
import org.wyx.diego.pontifex.bytecode.NebulaJavassistProxy;
import org.wyx.diego.pontifex.loader.handler.ComponentInvocationHandler;
import org.wyx.diego.pontifex.loader.runtime.RuntimeObject;

/**
 * @author wangyingxin
 * @title: ComponentLoaderInstance
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/29
 */
public enum ComponentLoaderInstance implements Loader<Component, Component>  {

    INSTANCE(new BaseComponentLoader());

    private BaseComponentLoader componentLoader;

    ComponentLoaderInstance(BaseComponentLoader componentLoader) {
        this.componentLoader = componentLoader;
    }


    @Override
    public Component load(Component component) {
        return componentLoader.load(component);
    }

    private static class BaseComponentLoader implements ComponentLoader<Component<?, ?>, Component<?, ?>> {

        public Component<?, ?> load(Component<?, ?> component) {
            ComponentMeta componentMeta = (ComponentMeta)component.getClass().getAnnotation(ComponentMeta.class);
            if (componentMeta == null) {
                return component;
            } else {
                ComponentInvocationHandler.ComponentProxy componentProxy = ComponentInvocationHandler.ComponentProxy.build(component);
                ComponentInvocationHandler componentInvocationHandler = new ComponentInvocationHandler(componentProxy);
                Component componentP = (Component)NebulaJavassistProxy.getProxy(component.getClass()).newInstance(componentInvocationHandler);
                return componentP;
            }
        }
    }

}
