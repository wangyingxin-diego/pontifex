package org.wyx.diego.pontifex.demo.business.user;

import org.springframework.stereotype.Component;
import org.wyx.diego.pontifex.annotation.TaskMeta;
import org.wyx.diego.pontifex.exception.BusinessException;
import org.wyx.diego.pontifex.exception.ExceptionLevel;
import org.wyx.diego.pontifex.pipeline.TaskContext;
import org.wyx.diego.pontifex.pipeline.PontifexTaskSortConstant;
import org.wyx.diego.pontifex.pipeline.Task;

import java.util.ArrayList;
import java.util.List;

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
            throwException(new BusinessException() {
                @Override
                public int getCode() {
                    return 0;
                }

                @Override
                public String getMsg() {
                    return null;
                }

                @Override
                public ExceptionLevel getExceptionLevel() {
                    return null;
                }
            });
        }

        ctx.getPontifexResult().setDepartment("eeeeeee");
        UserResponse userResponse = new UserResponse();
        userResponse.setDepartment("eeeeeeeeee");
        ctx.getPontifexResult().setUserResponse(userResponse);

        List<UserResponse> userResponseList = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            UserResponse userResponse1 = new UserResponse();
            userResponse.setDepartment("eeeeeeeeee");
            userResponseList.add(userResponse1);
        }
        ctx.getPontifexResult().setUserResponses(userResponseList);

        System.out.println("user add send message!");



    }

    @Override
    public int getType() {
        return 0;
    }
}
