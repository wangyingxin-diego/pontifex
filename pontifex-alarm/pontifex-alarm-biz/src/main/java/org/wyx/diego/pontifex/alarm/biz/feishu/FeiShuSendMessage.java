package org.wyx.diego.pontifex.alarm.biz.feishu;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.alarm.AlarmMessage;
import org.wyx.diego.pontifex.alarm.biz.SendMessage;
import org.wyx.diego.pontifex.alarm.biz.SendResult;
import org.wyx.diego.pontifex.utils.HttpClient;

public class FeiShuSendMessage implements SendMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeiShuSendMessage.class);
    private String url;

    public FeiShuSendMessage(String url) {
        this.url = url;
    }

    @Override
    public SendResult sendMessage(AlarmMessage alarmMessage) {

        if(url == null || "".equals(url.trim())) {
            return SendResult.ofSuc();
        }

        String sendUrl = alarmMessage.getSendUrl();
        if(sendUrl == null || "".equals(sendUrl.trim())) {
            sendUrl = url;
        }
        if(sendUrl == null || "".equals(sendUrl.trim())) {
            return SendResult.ofFail();
        }

        String result = HttpClient.doJsonPost(sendUrl, JSONObject.toJSONString(alarmMessage));

        return SendResult.ofSuc();
    }
}
