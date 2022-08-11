package com.ayuan.mewtwo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * http server based netty
 *
 * @author Ayuan
 */
@Slf4j
public class MewtwoHttpServer {

    /**
     * host
     */
    private final static String HOST = "127.0.0.1";

    /**
     * 端口号
     */
    private final static Integer PORT = 8085;

    /**
     * netty服务端启动方法
     */
    public void start() {
        log.info("超梦启动中...");
        EventLoopGroup boos = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boos, worker)
                    .channel(NioServerSocketChannel.class)
                    //开启tcp nagle算法
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //开启长链接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new HttpRequestDecoder())
                            .addLast(new HttpResponseEncoder())
                            .addLast(new HttpObjectAggregator(512 * 1024))
                            .addLast(new MewtwoHttpServerHandler());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(HOST,PORT).sync();

            log.info("超梦启动成功 端口:{}",PORT);

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("超梦启动失败,错误原因:", e);
        } finally {
            log.info("关闭 boss worker...");
            boos.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
