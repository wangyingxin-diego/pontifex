package org.wyx.diego.pontifex.demo.business.user;

import org.wyx.diego.pontifex.EncryptionDecryptionLabel;
import org.wyx.diego.pontifex.Response;

import java.util.List;

public class UserResponse implements Response, EncryptionDecryptionLabel {


    private long userId;

//    @Encryption
    private String department;

//    @Encryption
    private UserResponse userResponse;

//    @Encryption
    private List<UserResponse> userResponses;

    public String getDepartment() {
        return department;
    }

    public UserResponse setDepartment(String department) {
        this.department = department;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public UserResponse getUserResponse() {
        return userResponse;
    }

    public UserResponse setUserResponse(UserResponse userResponse) {
        this.userResponse = userResponse;
        return this;
    }

    public List<UserResponse> getUserResponses() {
        return userResponses;
    }

    public UserResponse setUserResponses(List<UserResponse> userResponses) {
        this.userResponses = userResponses;
        return this;
    }
}
