package org.wyx.diego.pontifex.demo.business.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wyx.diego.pontifex.annotation.ComponentMeta;
import org.wyx.diego.pontifex.component.BaseComponent;
import org.wyx.diego.pontifex.component.BaseComponentReq;
import org.wyx.diego.pontifex.component.BaseComponentRes;
import org.wyx.diego.pontifex.component.Res1;
import org.wyx.diego.pontifex.log.ComponentLogLevel;

@Component
@ComponentMeta(name = "UserComponent", level = ComponentLogLevel.ALL)
public class UserComponent extends BaseComponent<UserComponent.UserComponentReq, UserComponent.UserComponentRes> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserComponent.class);


    @Override
    public Res1<UserComponentRes> call(UserComponentReq req) {

        long userId = req.getUserId();
        String userName = req.getUserName();

        LOGGER.info("pontifex userComponent ");

        UserComponentRes userComponentRes = new UserComponentRes();
        userComponentRes.setUserId(userId).setUserName(userName).setPhone("19898989898").setAddress("beijing");

        Res1<UserComponentRes> resRes1 = new Res1<>(userComponentRes);

        return resRes1;
    }

    public static class UserComponentReq extends BaseComponentReq {

        @Override
        public String getKey() {
            return null;
        }


        private long userId;

        private String userName;

        public long getUserId() {
            return userId;
        }

        public UserComponentReq setUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public String getUserName() {
            return userName;
        }

        public UserComponentReq setUserName(String userName) {
            this.userName = userName;
            return this;
        }
    }

    public static class UserComponentRes extends BaseComponentRes {

        private long userId;

        private String userName;

        private String address;

        private String phone;

        public long getUserId() {
            return userId;
        }

        public UserComponentRes setUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public String getUserName() {
            return userName;
        }

        public UserComponentRes setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public String getAddress() {
            return address;
        }

        public UserComponentRes setAddress(String address) {
            this.address = address;
            return this;
        }

        public String getPhone() {
            return phone;
        }

        public UserComponentRes setPhone(String phone) {
            this.phone = phone;
            return this;
        }
    }

}
