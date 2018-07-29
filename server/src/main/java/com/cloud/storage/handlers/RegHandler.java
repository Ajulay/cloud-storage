package com.cloud.storage.handlers;

import com.cloud.storage.common.TransmitClass;
import com.cloud.storage.dao.SQLHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.sql.SQLException;

import static com.cloud.storage.common.UtilConstants.REG_INFO;

public class RegHandler extends ChannelInboundHandlerAdapter {
    TransmitClass tc;
    String s;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        tc = (TransmitClass)msg;
        String[] userData = new String(tc.getBuffer()).split("\\s");
        if(tc.getMark()==REG_INFO) {
          s = new String(tc.getBuffer()); // System.out.println("Sec: " + Arrays.toString(arr));

            SQLHandler.connect();
            SQLHandler.addClientData(userData[0], Long.parseLong(userData[1]));

            tc = new TransmitClass("/regok", REG_INFO);

            ctx.write(tc);
            ctx.flush();
            ctx.pipeline().remove(this);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        tc = new TransmitClass("Wrong registration. Change login and try again or check connection...", REG_INFO);
        ctx.write(tc);
        ctx.flush();
       // ctx.pipeline().remove(this);
    }
}
