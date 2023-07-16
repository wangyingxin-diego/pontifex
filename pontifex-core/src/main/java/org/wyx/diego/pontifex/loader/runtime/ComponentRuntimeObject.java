package org.wyx.diego.pontifex.loader.runtime;

import org.wyx.diego.pontifex.Component;
import org.wyx.diego.pontifex.annotation.ComponentMeta;
import org.wyx.diego.pontifex.annotation.RuntimeMeta;
import org.wyx.diego.pontifex.log.ComponentLogLevel;
import org.wyx.diego.pontifex.ModuleType;

/**
 * @author wangyingxin
 * @title: ComponentRuntimeObject
 * @projectName pontifex
 * @description: TODO
 * @date 2015/12/31
 */
public class ComponentRuntimeObject extends RuntimeObject {

    private final String name;
    private final ComponentLogLevel componentLogLevel;
    private final Async async;

    public ComponentRuntimeObject(Component component) {
        ComponentMeta componentMeta = component.getClass().getAnnotation(ComponentMeta.class);
        if (componentMeta == null) {
            throw new NullPointerException();
        }

        RuntimeMeta runtimeMeta = componentMeta.runtime();
        super.open = runtimeMeta.open();
        super.timeout = runtimeMeta.timeout();
        super.alarms = this.analysisAlarm(runtimeMeta.alarm());
        super.moduleType = ModuleType.MODULE_TYPE_COMPONENT;
        super.cacheBean = super.analysisCache(componentMeta.cache());
        String name = componentMeta.name();
        if (name != null && !"".equals(name.trim())) {
            this.name = name;
        } else {
            this.name = component.getClass().getSimpleName();
        }

        this.componentLogLevel = componentMeta.level();
        org.wyx.diego.pontifex.annotation.Async asyncAnn = componentMeta.async();
        boolean isOpen = asyncAnn.isOpen();
        short concurrency = asyncAnn.concurrency();
        this.async = new Async(isOpen, concurrency);

        componentMeta.cache();

    }

    public String getName() {
        return this.name;
    }

    public ComponentLogLevel getComponentLogLevel() {
        return this.componentLogLevel;
    }

    public Async getAsync() {
        return this.async;
    }

}
