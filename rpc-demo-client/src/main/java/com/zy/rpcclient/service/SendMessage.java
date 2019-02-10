package com.zy.rpcclient.service;

import com.zy.rpccommon.annotation.RpcClient;

/**
 * Created by Horizon Time: 14:55 2019-02-10 Description:
 */
@RpcClient
public interface SendMessage {

    String sendName(String name);

}
