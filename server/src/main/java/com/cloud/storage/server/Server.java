package com.cloud.storage.server;

import com.cloud.storage.dao.SQLHandler;
import com.cloud.storage.handlers.AuthHandler;
import com.cloud.storage.handlers.RegHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.sql.SQLException;


public class Server {

   private static final int MAX_OBJ_SIZE = 1024 * 1024 * 100;
   public static final String CLIENT_DIRS = "server/src/main/java/com/cloud/storage/clientdirs/";

public void run(){

    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
    ServerBootstrap b = new ServerBootstrap();
    ServerBootstrap serverBootstrap = b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new ChannelInitializer() {
                              @Override
                              protected void initChannel(Channel ch){
                                ch.pipeline().addLast(new ObjectDecoder(MAX_OBJ_SIZE, ClassResolvers.cacheDisabled(null)),
                                                      new ObjectEncoder(),
                                                      new AuthHandler());
                              }


                          }
            )
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);


        ChannelFuture channelFuture = b.bind(3456).sync();



        channelFuture.channel().closeFuture().sync();

    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    finally {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
            try {
                SQLHandler.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

}



}
