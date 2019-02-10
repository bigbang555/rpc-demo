package com.zy.rpcclient.init;

import com.zy.rpccommon.pojo.Request;
import com.zy.rpccommon.pojo.Response;
import com.zy.rpccommon.util.ProtostuffUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.springframework.stereotype.Component;

/**
 * Created by Horizon Time: 11:44 2019-02-10 Description:
 */
@Component
public class RpcDynamicPro implements InvocationHandler {

    private  Request request;

    private  Response response;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("执行invoke");
        objectToByte(method, args);
        String ip = "localhost";
        int port = 20000;
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();
        client.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(final SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new TransferChannelAdapter());
                    }
                });

        try {
            ChannelFuture future = client.connect(ip, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
        return response.getResult();
    }

    private void objectToByte(Method method, Object[] args) {
        request = new Request();
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String className = method.getDeclaringClass().getName();
        request.setClassName(getClassName(className));
        request.setMethodName(methodName);
        request.setParamTyprs(parameterTypes);
        request.setParameters(args);
    }

    private String getClassName(String beanClassName) {
        String className = beanClassName.substring(beanClassName.lastIndexOf(".") + 1);
        className = className.substring(0, 1).toLowerCase() + className.substring(1);
        return className;
    }

    private class TransferChannelAdapter extends ChannelInboundHandlerAdapter {


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println(1);
            ctx.writeAndFlush(ProtostuffUtils.serialize(request));
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println(1);
        }


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            response = ProtostuffUtils.deserialize((byte[]) msg, Response.class);
            ctx.close();
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }


}
