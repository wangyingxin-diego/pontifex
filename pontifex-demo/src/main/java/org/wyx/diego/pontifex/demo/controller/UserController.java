package org.wyx.diego.pontifex.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.wyx.diego.pontifex.NullResponse;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.demo.business.user.UserRequest;
import org.wyx.diego.pontifex.manager.DefaultPontifexManagerInstance;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {


    /**
     * {@link org.wyx.diego.pontifex.demo.business.user.UserAddTask}
     * @param pontifexRequest
     * @return
     */
    @PostMapping("/add")
    public PontifexResponse userAdd(@Valid PontifexRequest<UserRequest> pontifexRequest) {

        PontifexResponse pontifexResponse = DefaultPontifexManagerInstance.INSTANCE.handler(pontifexRequest);

        pontifexResponse.setResult(new NullResponse());

        return pontifexResponse;

    }

    @GetMapping("/add2")
    public PontifexResponse userAdd2(@Valid PontifexRequest<UserRequest> pontifexRequest) {
        pontifexRequest.setSecretKey("123456");

        return DefaultPontifexManagerInstance.INSTANCE.handler(pontifexRequest);

    }

    @GetMapping("/add3")
    public PontifexResponse userAdd3(UserRequest userRequest) {

        PontifexRequest pontifexRequest1 = new PontifexRequest();
        pontifexRequest1.setBizObject(userRequest);
        return DefaultPontifexManagerInstance.INSTANCE.handler(pontifexRequest1);

    }

}
