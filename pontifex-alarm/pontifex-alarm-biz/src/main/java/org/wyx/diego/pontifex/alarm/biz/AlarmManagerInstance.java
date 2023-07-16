package org.wyx.diego.pontifex.alarm.biz;

public enum AlarmManagerInstance {
    INSTANCE(new AlarmManager());

    private AlarmManager alarmManager;

    AlarmManagerInstance(AlarmManager alarmManager) {
        this.alarmManager = alarmManager;
    }

    public AlarmManager getAlarmManager() {
        return alarmManager;
    }
}
