package org.wyx.diego.pontifex.demo.business.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wyx.diego.pontifex.annotation.TaskMeta;
import org.wyx.diego.pontifex.demo.business.component.UserComponent;
import org.wyx.diego.pontifex.pipeline.TaskContext;
import org.wyx.diego.pontifex.component.Res1;
import org.wyx.diego.pontifex.pipeline.PontifexTaskSortConstant;
import org.wyx.diego.pontifex.pipeline.Task;

import static org.wyx.diego.pontifex.demo.business.user.UserTaskConstants.USER_ADD_PIPELINE;

@TaskMeta(pipelineName = USER_ADD_PIPELINE, name = "UserAddTask", sort = PontifexTaskSortConstant.PONTIFEX_TASK_SORT_FIRST)
@Component
public class UserAddTask extends Task<UserRequest, UserPayload, UserResponse> {

    @Autowired
    private UserComponent userComponent;
    /**
     * {@link cn.cu.online.mail.business.user.UserAddSendMessageTask}
     * @param ctx
     */
    @Override
    public void run(TaskContext<UserRequest, UserPayload, UserResponse> ctx) {

        UserRequest userRequest = ctx.getPontifexRequest().getBizObject();
        Res1<UserComponent.UserComponentRes> resRes1 = userComponent.call(new UserComponent.UserComponentReq().setUserId(1000L).setUserName("lisi"));
        UserComponent.UserComponentRes userComponentRes = resRes1.getR1();

        System.out.println("user insert db.");
        UserPayload userPayload = ctx.getPayload();
        userPayload.setUserId(10009);
        userPayload.setUserName("xiaoming");

    }

    @Override
    public int getType() {
        return 0;
    }
}
