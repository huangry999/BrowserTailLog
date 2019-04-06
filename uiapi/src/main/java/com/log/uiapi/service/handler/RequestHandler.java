package com.log.uiapi.service.handler;

import com.log.uiapi.protocol.logp.LogP;
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

    /**
     * authenticate the request
     *
     * @param ctx context
     * @param msg frame
     * @throws Exception exception
     */

    void authorization(ChannelHandlerContext ctx, LogP msg) throws Exception;
}
