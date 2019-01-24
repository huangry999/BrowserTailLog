package com.log.socket.handler;

import com.log.socket.constants.RespondStatus;
import com.log.socket.exception.LogPException;
import com.log.socket.logp.LogP;
import com.log.socket.logp.LogPFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ByteBuf buf = ctx.alloc().directBuffer().copy();
        byte[] frame = new byte[buf.readableBytes()];
        buf.readBytes(frame);
        if (cause instanceof LogPException) {
            final RespondStatus respondStatus = ((LogPException) cause).getRespondStatus();
            switch (respondStatus) {
                case UNAUTHORIZED:
                    LogP response = LogPFactory.defaultInstance0().create0(respondStatus, cause.getMessage());
                    ctx.channel().writeAndFlush(response);
                    return;
            }
        }
        logger.error("{} catch exception -- ", ctx.channel().remoteAddress(), cause);
        CloseWebSocketFrame response = new CloseWebSocketFrame(RespondStatus.INTERNAL_SERVER_ERROR.getCode(), cause.getMessage());
        ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
