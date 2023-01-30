package org.wyx.diego.pontifex.loader.runtime;


import org.wyx.diego.pontifex.ModuleType;
import org.wyx.diego.pontifex.annotation.Alarm;
import org.wyx.diego.pontifex.annotation.RuntimeMeta;
import org.wyx.diego.pontifex.cache.CacheBean;

import java.util.Arrays;
import java.util.List;

public abstract class RuntimeObject {
    protected ModuleType moduleType;
    protected boolean open;
    protected long timeout;
    protected List<Alarm> alarms;

    protected CacheBean cacheBean;

    protected RuntimeObject() {
    }

    protected RuntimeObject(ModuleType moduleType, boolean open, long timeout, List<Alarm> alarms) {
        this.moduleType = moduleType;
        this.open = open;
        this.timeout = timeout;
        this.alarms = alarms;
    }

    public RuntimeObject(RuntimeMeta runtimeMeta, ModuleType moduleType) {
        this.moduleType = moduleType;
        this.open = runtimeMeta.open();
        long timeout = runtimeMeta.timeout();
        if (timeout < 1L) {
            this.timeout = 50L;
        } else {
            this.timeout = timeout;
        }

        Alarm[] alarms = runtimeMeta.alarm();
        this.alarms = this.analysisAlarm(alarms);
    }

    protected List<Alarm> analysisAlarm(Alarm[] alarms) {
        if (alarms == null || alarms.length == 0) {
            Alarm[] temp = new Alarm[]{Alarm.LOG};
            alarms = temp;
        }

        return Arrays.asList(alarms);
    }

    protected CacheBean analysisCache(org.wyx.diego.pontifex.annotation.Cache cache) {
        this.cacheBean = new CacheBean(cache);
        return this.cacheBean;
    }

    public ModuleType getModuleType() {
        return this.moduleType;
    }

    public boolean isOpen() {
        return this.open;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public List<Alarm> getAlarms() {
        return this.alarms;
    }

    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    public CacheBean getCache() {
        return cacheBean;
    }

    public void setCache(CacheBean cacheBean) {
        this.cacheBean = cacheBean;
    }
}
