package com.log.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.log.socket.logp.LogP;
import com.log.subscribe.SubscriberManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import org.apache.logging.log4j.util.Strings;

public abstract class BasicRequestHandler implements RequestHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, LogP msg) throws Exception {
        String body = msg.getBody();
        CommonRequest request = this.parseBody(body);
        this.handle(ctx, msg, request);
        ctx.channel().closeFuture().addListener(future -> this.onClose(ctx, future));
    }

    /**
     * close socket callback interface to override
     * @param context context
     * @param future close future
     */
    protected void onClose(ChannelHandlerContext context, Future future){

    }

    /**
     * parse body to java bean
     *
     * @param body string body
     * @return CommonRequest or child class
     * @throws Exception exception
     */
    protected CommonRequest parseBody(String body) throws Exception {
        if (Strings.isBlank(body)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(body, CommonRequest.class);
    }

    /**
     * simple handle
     *
     * @param ctx     context
     * @param msg     frame
     * @param request request
     * @throws Exception exception
     */
    protected abstract void handle(ChannelHandlerContext ctx, LogP msg, CommonRequest request) throws Exception;

}
