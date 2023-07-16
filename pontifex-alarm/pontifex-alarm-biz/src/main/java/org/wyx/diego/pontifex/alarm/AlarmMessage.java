package org.wyx.diego.pontifex.alarm;

import org.wyx.diego.pontifex.annotation.Alarm;

import java.io.Serializable;
import java.util.List;

public class AlarmMessage implements Serializable {

    private String name;

    private long expectedTime;

    private long actualTime;

    private String sendUrl;

    private List<Alarm> alarms;

    public AlarmMessage(String name, long expectedTime, long actualTime, String sendUrl, List<Alarm> alarms) {
        this.name = name;
        this.expectedTime = expectedTime;
        this.actualTime = actualTime;
        this.sendUrl = sendUrl;
        this.alarms = alarms;
    }

    public String getName() {
        return name;
    }

    public long getExpectedTime() {
        return expectedTime;
    }

    public long getActualTime() {
        return actualTime;
    }

    public String getSendUrl() {
        return sendUrl;
    }

    public List<Alarm> getAlarms() {
        return alarms;
    }
}
