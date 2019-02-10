package com.zy.rpccommon.pojo;

import lombok.Data;

/**
 * Created by Horizon Time: 10:57 2019-02-10 Description:
 */
@Data
public class Request {

    private String className;

    private String methodName;

    private Class<?>[] paramTyprs;

    private Object[] parameters;



}
