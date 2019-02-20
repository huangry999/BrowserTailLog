package com.lookforlog.log.socket.handler;

import com.lookforlog.log.service.handler.RequestHandler;
import com.lookforlog.log.subscribe.SubscriberManager;
import com.lookforlog.protocol.logp.LogP;
import com.lookforlog.util.SpringUtils;
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
