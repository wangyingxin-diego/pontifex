package org.wyx.diego.pontifex.demo.business.user;

import org.wyx.diego.pontifex.Request;

import javax.validation.constraints.NotNull;

public class UserRequest implements Request {


    private Long userId;

    @NotNull
    private String userName;

    private String address;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
