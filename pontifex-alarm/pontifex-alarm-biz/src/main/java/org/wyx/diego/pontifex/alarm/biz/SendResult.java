package org.wyx.diego.pontifex.alarm.biz;

public class SendResult {

    private boolean result;

    public boolean isResult() {
        return result;
    }

    public SendResult setResult(boolean result) {
        this.result = result;
        return this;
    }

    public static SendResult ofSuc() {
        SendResult sendResult = new SendResult();
        sendResult.setResult(true);
        return sendResult;
    }

    public static SendResult ofFail() {
        SendResult sendResult = new SendResult();
        sendResult.setResult(false);
        return sendResult;
    }

}
