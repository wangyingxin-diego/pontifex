package org.wyx.diego.pontifex.demo.business.user;

import org.wyx.diego.pontifex.pipeline.Payload;

public class UserPayload implements Payload {

    private long userId;

    private String userName;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
