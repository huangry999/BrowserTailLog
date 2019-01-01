package com.log.service.handler;

import com.log.socket.logp.LogP;
import io.netty.channel.ChannelHandlerContext;

public interface RequestHandler {

    /**
     * handle the handler
     *
     * @param ctx context
     * @param msg frame
     * @throws Exception exception
     */
    void channelRead(ChannelHandlerContext ctx, LogP msg) throws Exception;
}