package com.zy.rpcclient.init;

import java.lang.reflect.Proxy;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Horizon Time: 11:43 2019-02-10 Description:
 */
@Data
public class RpcClientFactoryBean implements FactoryBean {

    @Autowired
    private RpcDynamicPro rpcDynamicPro;

    private Class<?> classType;

    public RpcClientFactoryBean(Class<?> classType) {
        this.classType = classType;
    }

    @Override
    public Object getObject() throws Exception {
        ClassLoader classLoader = classType.getClassLoader();
        return Proxy.newProxyInstance(classLoader, new Class<?>[]{classType}, rpcDynamicPro);
    }

    @Override
    public Class<?> getObjectType() {
        return this.classType;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
