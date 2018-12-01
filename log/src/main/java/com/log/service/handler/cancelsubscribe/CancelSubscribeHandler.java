package com.log.service.handler.cancelsubscribe;

import com.log.service.handler.BasicRequestHandler;
import com.log.service.handler.CommonRequest;
import com.log.socket.logp.LogP;
import com.log.subscribe.SubscriberManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CancelSubscribeHandler extends BasicRequestHandler {
    private final static Logger logger = LoggerFactory.getLogger(CancelSubscribeHandler.class);
    private final SubscriberManager subscriberManager;

    @Autowired
    public CancelSubscribeHandler(SubscriberManager subscriberManager) {
        this.subscriberManager = subscriberManager;
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, CommonRequest request) throws Exception {
        File log = new File(request.getPath());
        subscriberManager.remove(ctx, log);
        logger.debug("cancel subscribe {} from {}", log.getAbsolutePath(), ctx.channel().remoteAddress());
    }
}
