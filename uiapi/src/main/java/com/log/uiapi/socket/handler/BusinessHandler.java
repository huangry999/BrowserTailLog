package com.log.uiapi.socket.handler;

import com.log.uiapi.protocol.logp.LogP;
import com.log.uiapi.service.handler.RequestHandler;
import com.log.uiapi.spring.SpringUtils;
import com.log.uiapi.subscribe.SubscriberManager;
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

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SubscriberManager subscriberManager = SpringUtils.get(SubscriberManager.class);
        subscriberManager.remove(ctx);
    }
}
