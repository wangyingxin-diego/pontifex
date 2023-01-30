package org.wyx.diego.pontifex.demo.business.user;

import org.wyx.diego.pontifex.Response;

public class UserResponse implements Response {


    private long userId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
