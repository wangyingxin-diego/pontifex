package org.wyx.diego.pontifex.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.wyx.diego.pontifex.demo.business.user.UserPipeline;
import org.wyx.diego.pontifex.demo.business.user.UserRequest;
import org.wyx.diego.pontifex.manager.DefaultPontifexManagerInstance;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.demo.business.user.UserAddTask;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserPipeline userPipeline;


    /**
     * {@link UserAddTask}
     * @param pontifexRequest
     * @return
     */
//    @PipelineMeta(name = "userAddPipeline", cache = @Cache(isOpen = true))
    @PostMapping("/add")
    public PontifexResponse userAdd(@Valid PontifexRequest<UserRequest> pontifexRequest) {

        return userPipeline.call(pontifexRequest);

//        PontifexResponse pontifexResponse = DefaultPontifexManagerInstance.INSTANCE.handler(pontifexRequest);

//        pontifexResponse.setResult(new NullResponse());

//        return pontifexResponse;

    }

    @GetMapping("/add2")
    public PontifexResponse userAdd2(@Valid PontifexRequest<UserRequest> pontifexRequest) {
        pontifexRequest.setDecryptKey("123456");

        return userPipeline.call(pontifexRequest);
//        return DefaultPontifexManagerInstance.INSTANCE.handler(pontifexRequest);

    }

    @GetMapping("/add3")
    public PontifexResponse userAdd3(UserRequest userRequest) {

        PontifexRequest pontifexRequest1 = new PontifexRequest();
        pontifexRequest1.setBizObject(userRequest);
        return userPipeline.call(pontifexRequest1);
//        return DefaultPontifexManagerInstance.INSTANCE.handler(pontifexRequest1);

    }

}
