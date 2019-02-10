package com.zy.rpcclient.controller;

import com.zy.rpcclient.service.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Horizon Time: 15:03 2019-02-10 Description:
 */
@RestController
public class RpcController {

    @Autowired
    private SendMessage sendMessage;

    @GetMapping("/hello")
    public String getName() {
        return sendMessage.sendName("horizon");
    }

}
