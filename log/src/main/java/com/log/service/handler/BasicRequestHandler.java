package com.log.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.log.socket.logp.LogP;
import com.log.subscribe.SubscriberManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicRequestHandler<T extends RequestParam> implements RequestHandler {
    private final Class<T> clazz;

    public BasicRequestHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, LogP msg) throws Exception {
        this.authorization(ctx, msg);
        String body = msg.getBody();
        T request = this.parseBody(body);
        this.handle(ctx, msg, request);
    }

    @Override
    public void authorization(ChannelHandlerContext ctx, LogP msg) throws Exception {
        //do nothing
    }

    /**
     * parse body to java bean
     *
     * @param body body json string
     * @return PathRequest or child class
     * @throws Exception exception
     */
    protected T parseBody(String body) throws Exception {
        if (Strings.isBlank(body)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(body, clazz);
    }

    /**
     * simple handle
     *
     * @param ctx     context
     * @param msg     frame
     * @param request request
     * @throws Exception exception
     */
    protected abstract void handle(ChannelHandlerContext ctx, LogP msg, T request) throws Exception;
}
