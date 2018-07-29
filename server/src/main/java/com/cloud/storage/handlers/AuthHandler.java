package com.cloud.storage.handlers;

import com.cloud.storage.common.TransmitClass;
import com.cloud.storage.common.nets.MyNet;
import com.cloud.storage.dao.SQLHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.cloud.storage.common.UtilConstants.AUTH_INFO;
import static com.cloud.storage.common.UtilConstants.EXCEPTION;
import static com.cloud.storage.common.UtilConstants.REG_INFO;
import static com.cloud.storage.server.Server.CLIENT_DIRS;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    TransmitClass tc;
    String s;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        tc = (TransmitClass)msg;
        if(tc.getMark()==AUTH_INFO) {
            String[] userData = new String(tc.getBuffer()).split("\\s");
            SQLHandler.connect();
            s =  SQLHandler.getClient(userData[0], Long.parseLong(userData[1]));

            if(s.equals(new String(tc.getBuffer()))){
            Path dir = Paths.get(CLIENT_DIRS + userData[0]);
            if(!Files.exists(dir)) Files.createDirectory(dir);

            tc = new TransmitClass("/authok " + s);
            ctx.write(tc);
            ctx.flush();}

            ctx.pipeline().addLast(new OutHandler(),
                    new StringHandler(), new FileHandler()
            );
            ctx.pipeline().remove(this);

        }else if(tc.getMark()==REG_INFO){
            ctx.pipeline().addLast(new RegHandler());
            ctx.fireChannelRead(tc);
        }else
            ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        tc = new TransmitClass("Try again...", EXCEPTION);
        ctx.write(tc);
        ctx.flush();
    }
}
