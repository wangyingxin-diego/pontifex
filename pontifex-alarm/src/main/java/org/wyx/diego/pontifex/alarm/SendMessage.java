package org.wyx.diego.pontifex.alarm;

public interface SendMessage {

    SendResult sendMessage(String url, Message message);

}
