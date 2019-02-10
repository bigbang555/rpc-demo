package com.zy.rpcserver.init;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.zy.rpccommon.pojo.Request;
import com.zy.rpccommon.pojo.Response;
import com.zy.rpccommon.util.ProtostuffUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by Horizon Time: 15:22 2019-02-10 Description:
 */
@Component
public class InitRpcConfig implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;

    private Map<String, Object> rpcServiceap = new HashMap<>();

    @Override
    public void run(String... args) throws Exception {
        Map<String, Object> beansWithAnnotation = applicationContext
                .getBeansWithAnnotation(Service.class);
        for (Object bean : beansWithAnnotation.values()) {
            Class<?> clazz = bean.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> inter : interfaces) {
                rpcServiceap.put(getClassName(inter.getName()), bean);
                System.out.println("已经加载的服务:" + inter.getName());
            }
        }
        startNetty();

    }

    private String getClassName(String beanClassName) {
        String className = beanClassName.substring(beanClassName.lastIndexOf(".") + 1);
        className = className.substring(0, 1).toLowerCase() + className.substring(1);
        return className;
    }

    public void startNetty() throws InterruptedException {
//        EventLoopGroup boss = new NioEventLoopGroup();
//        EventLoopGroup worker = new NioEventLoopGroup();
//        try {
//            ServerBootstrap server = new ServerBootstrap();
//            server.group(boss, worker)
//                    .channel(NioServerSocketChannel.class)
//                    .option(ChannelOption.SO_BACKLOG, 128)
//                    .childOption(ChannelOption.SO_KEEPALIVE, false)
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel channel) throws Exception {
//                            ChannelPipeline pipeline = channel.pipeline();
//                            pipeline.addLast(new TransferChannelAdapter());
//                        }
//                    });
//            ChannelFuture future = server.bind(20000).sync();
//            System.out.println("Netty服务已启动");
//
//            future.channel().closeFuture().sync();
//        } finally {
//            worker.shutdownGracefully();
//            boss.shutdownGracefully();
//
//        }
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new StringDecoder());
                        p.addLast(new StringEncoder());
                        p.addLast(new TransferChannelAdapter());
                    }
                });
        bootstrap.bind(20000).sync();

    }

    private class TransferChannelAdapter extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Request request = ProtostuffUtils.deserialize((byte[]) msg, Request.class);
            Response response = invokeMethod(request);
            ctx.writeAndFlush(ProtostuffUtils.serialize(response));
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelActive");
        }

        private Response invokeMethod(Request request)
                throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            String className = request.getClassName();
            String methodName = request.getMethodName();
            Class<?>[] paramTyprs = request.getParamTyprs();
            Object[] parameters = request.getParameters();
            Object o = rpcServiceap.get(className);
            Method method = o.getClass().getDeclaredMethod(methodName, paramTyprs);
            Object result = method.invoke(o, parameters);
            Response response = new Response();
            response.setResult(result);
            return response;
        }
    }
}
