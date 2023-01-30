package org.wyx.diego.pontifex.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.demo.business.user.UserRequest;
import org.wyx.diego.pontifex.manager.DefaultPontifexManagerInstance;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {


    /**
     * {@link cn.cu.online.mail.business.user.UserAddTask}
     * @param pontifexRequest
     * @return
     */
    @PostMapping("/add")
    public PontifexResponse userAdd(@RequestBody @Valid PontifexRequest<UserRequest> pontifexRequest) {

        return DefaultPontifexManagerInstance.INSTANCE.handler(pontifexRequest);

    }

}
