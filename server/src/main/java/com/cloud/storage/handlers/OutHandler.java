package com.cloud.storage.handlers;

import com.cloud.storage.common.TransmitClass;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static com.cloud.storage.common.UtilConstants.FILE;
import static com.cloud.storage.server.Server.CLIENT_DIRS;

public class OutHandler extends ChannelOutboundHandlerAdapter {
    TransmitClass tc;
    String s;
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //передать запрашиваемый файл
        tc = (TransmitClass) msg;
      //  System.out.println("OutHandler" + tc.getFileName());
        if(tc.getMark()==FILE) {
          //  System.out.println("OutHandler" + new String(tc.getBuffer()));
            ctx.writeAndFlush(tc);
        }else {
            //обойти дерево файлов, отправить информацию о именах хранящихся файлов если производилось удаление, добавление или запрос при загрузке

            List<String> filelist = new ArrayList<>();
           // System.out.println(tc.getFileName().split("/")[0]);
            Files.walkFileTree(Paths.get(CLIENT_DIRS + tc.getFileName().split("/")[0]), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    filelist.add(file.getFileName().toString() + ", " + file.toFile().length() + " byte(s)");
                    return FileVisitResult.CONTINUE;
                }
            });

            s = "/filesInCloud\n";
            for (int i = 0; i < filelist.size(); i++) {
                s += filelist.get(i);
                if (i < filelist.size() - 1) {
                    s += "\n";
                }
            }
          //  System.out.println(s);
            tc = new TransmitClass(s);
            ctx.write(tc);
            ctx.flush();
          //  tc = new TransmitClass("Operation is interrupted...");
           // ctx.writeAndFlush(tc);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //передать информацию (операция прервана, повторите попытку)
        tc = new TransmitClass("Operation is interrupted...");
        ctx.writeAndFlush(tc);
        System.out.println("Out");
    }
}
