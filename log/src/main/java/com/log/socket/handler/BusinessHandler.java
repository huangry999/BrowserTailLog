package com.log.socket.handler;

import com.log.service.handler.RequestHandler;
import com.log.socket.logp.LogP;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class BusinessHandler extends SimpleChannelInboundHandler<LogP> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, LogP msg) throws Exception {
        RequestHandler handler = msg.getHead().getRequest().handler();
        handler.channelRead(ctx, msg);
    }
}
