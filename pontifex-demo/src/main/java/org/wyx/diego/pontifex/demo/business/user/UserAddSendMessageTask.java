package org.wyx.diego.pontifex.demo.business.user;

import org.springframework.stereotype.Component;
import org.wyx.diego.pontifex.annotation.TaskMeta;
import org.wyx.diego.pontifex.pipeline.PontifexTaskSortConstant;
import org.wyx.diego.pontifex.pipeline.Task;
import org.wyx.diego.pontifex.pipeline.TaskContext;

@TaskMeta(pipelineName = UserTaskConstants.USER_ADD_PIPELINE, name = "UserAddSendMessageTask", sort = PontifexTaskSortConstant.PONTIFEX_TASK_SORT_FIVE)
@Component
public class UserAddSendMessageTask extends Task<UserRequest, UserPayload, UserResponse> {

    /**
     * * {@link cn.cu.online.mail.business.user.UserAddTask}
     * @param ctx
     */
    @Override
    public void run(TaskContext<UserRequest, UserPayload, UserResponse> ctx) {

        UserPayload userPayload = ctx.getPayload();
        long userId = userPayload.getUserId();
        String userName = userPayload.getUserName();

        if(userId == 1000L) {
            throwException(1000, "user error");
        }

        System.out.println("user add send message!");



    }

    @Override
    public int getType() {
        return 0;
    }
}
