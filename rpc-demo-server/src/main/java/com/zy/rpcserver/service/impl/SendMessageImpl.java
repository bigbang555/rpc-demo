package com.zy.rpcserver.service.impl;

import com.zy.rpcserver.service.SendMessage;
import org.springframework.stereotype.Service;

/**
 * Created by Horizon Time: 14:56 2019-02-10 Description:
 */
@Service
public class SendMessageImpl implements SendMessage {

    @Override
    public String sendName(String name) {
        return "rpc-server echo: " + name;
    }
}
