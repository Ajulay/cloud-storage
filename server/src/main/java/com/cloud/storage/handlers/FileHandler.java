package com.cloud.storage.handlers;

import com.cloud.storage.common.TransmitClass;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.PriorityQueue;

import static com.cloud.storage.common.UtilConstants.FILE;
import static com.cloud.storage.common.UtilConstants.STRING;
import static com.cloud.storage.server.Server.CLIENT_DIRS;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

public class FileHandler extends ChannelInboundHandlerAdapter {
TransmitClass tc;
String s;
boolean longFile = false;
    Thread t = null;
final PriorityQueue<TransmitClass> queue = new PriorityQueue<>(new Comparator<TransmitClass>() {
        @Override
        public int compare(TransmitClass o1, TransmitClass o2) {
            return o1.getPart() - o2.getPart();
        }
    });

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        tc = (TransmitClass)msg;

        if(tc.getMark()==FILE){
            s = tc.getFileName();
            if(tc.getPart()==0) {

                Path p = Paths.get(CLIENT_DIRS, s);
                if (Files.exists(p)) Files.delete(p);
                Files.createFile(p);
                FileOutputStream out = new FileOutputStream(p.toFile());
                out.write(tc.getBuffer());
                out.flush();

                transport(ctx, s + "/file wrote...");

            } else if(!longFile){
                longFile = true;

                s=tc.getFileName();

                t = new Thread(new Runnable() {
                   int part = 1;

                   @Override
                   public void run() {
                       TransmitClass tct;
                       Path p;
                       FileOutputStream out = null;

                       try {
                       while(true) {
                           try {
                               while(queue.size()==0){
                                   sleep(10);}

                           } catch (InterruptedException e) {
                               System.out.println("Writing interrupt...");
                               //break;
                           }



                           if (queue.size() > 0) {
                               if (part == queue.peek().getPart()) {
                                   tct = queue.poll();
                                   p = Paths.get(CLIENT_DIRS, tct.getFileName());
                                   if (Files.exists(p)&&part==1) {
                                       Files.delete(p);
                                       Files.createFile(p);
                                   }

                                   part++;

                                   if (out == null)
                                       out = new FileOutputStream(p.toFile(), true);

                                   out.write(tct.getBuffer());
                                   out.flush();
                                  // System.out.println("=====");
                                   if(tct.isLastPart()){
                                     //  tct = null;
                                //   if(Thread.currentThread().isInterrupted() && queue.size()==0) // почему-то не срабатывает, пришлось заменить...???
                                       break;
                                   }
                               }
                           }
                       }
                       }catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
               });
               t.start();



            } if(longFile){
                queue.offer(tc);
                if(tc.isLastPart()){

                  // t.interrupt();
                    t.join();

                    longFile = false;
                    s=tc.getFileName();

                    transport(ctx, s + "/file wrote...");
                }
            }
        }
    }
    private void transport (ChannelHandlerContext ctx, String msg){
        tc = new TransmitClass(msg);
        tc.setFileName(s.split("/")[0]);
        ctx.write(tc);
        ctx.flush();

    }
}
