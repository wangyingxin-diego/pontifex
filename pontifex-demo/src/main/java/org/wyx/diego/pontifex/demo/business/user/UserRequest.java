package org.wyx.diego.pontifex.demo.business.user;

import org.wyx.diego.pontifex.BaseRequest;
import org.wyx.diego.pontifex.annotation.Decryption;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

public class UserRequest extends BaseRequest {


    @NotNull
    private Long userId;

    @NotNull
    private String userName;

    private String address;

    @NotNull
    private List<String> friends;

    @Decryption(name = "department")
    private String department;

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

    public String getDepartment() {
        return department;
    }

    public UserRequest setDepartment(String department) {
        this.department = department;
        return this;
    }

    public List<String> getFriends() {
        return friends;
    }

    public UserRequest setFriends(List<String> friends) {
        this.friends = friends;
        return this;
    }
}
