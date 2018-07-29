package com.cloud.storage.handlers;

import com.cloud.storage.common.TransmitClass;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.cloud.storage.common.UtilConstants.FILE;
import static com.cloud.storage.common.UtilConstants.STRING;
import static com.cloud.storage.server.Server.CLIENT_DIRS;


public class StringHandler extends ChannelInboundHandlerAdapter {
    TransmitClass tc;
    String s;
    String[] arr;
    Path p;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        tc = (TransmitClass)msg;
        if(tc.getMark()!=STRING){
            ctx.fireChannelRead(msg);

        }else {

            s = new String(tc.getBuffer());

            if (s.startsWith("/delete ")) {
                p = Paths.get(CLIENT_DIRS, tc.getFileName());
                if(Files.exists(p)) Files.delete(p);
                ctx.writeAndFlush(tc);

            }
            if (s.startsWith("/requestFile ")) {
                s = s.substring(s.indexOf(" ") + 1);
                p = Paths.get(CLIENT_DIRS, s);

                byte[] bb = new byte[(int) p.toFile().length()];//fix in future
                FileInputStream fis = new FileInputStream(p.toFile());
                fis.read(bb);
                tc = new TransmitClass(s, bb, 0, FILE);
                ctx.writeAndFlush(tc);
            }
            if (s.startsWith("/getFiles")) {
                ctx.write(tc);
                ctx.flush();


            }

        }
    }
}
