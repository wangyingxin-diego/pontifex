package org.wyx.diego.pontifex.alarm.biz;

import org.wyx.diego.pontifex.alarm.AlarmMessage;

public interface SendMessage {

    SendResult sendMessage(AlarmMessage alarmMessage);

}
