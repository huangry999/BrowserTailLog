package com.log.uiapi.socket.handler;

import com.log.uiapi.protocol.constants.RespondStatus;
import com.log.uiapi.protocol.exception.LogPException;
import com.log.uiapi.protocol.logp.LogP;
import com.log.uiapi.protocol.logp.LogPFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class ExceptionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ByteBuf buf = ctx.alloc().directBuffer().copy();
        byte[] frame = new byte[buf.readableBytes()];
        buf.readBytes(frame);
        if (cause instanceof LogPException) {
            final RespondStatus respondStatus = ((LogPException) cause).getRespondStatus();
            if (respondStatus == RespondStatus.UNAUTHORIZED) {
                LogP response = LogPFactory.defaultInstance0().create0(respondStatus, cause.getMessage());
                ctx.channel().writeAndFlush(response);
            }
        } else {
            log.error("{} catch exception -- ", ctx.channel().remoteAddress(), cause);
            LogP response = LogPFactory.defaultInstance0().create0(RespondStatus.INTERNAL_SERVER_ERROR, cause.getMessage());
            ctx.channel().writeAndFlush(response);
        }
    }
}
