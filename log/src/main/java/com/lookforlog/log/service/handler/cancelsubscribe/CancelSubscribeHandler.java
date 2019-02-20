package com.lookforlog.log.service.handler.cancelsubscribe;

import com.lookforlog.log.service.handler.BasicAuthRequestHandler;
import com.lookforlog.log.service.handler.PathRequest;
import com.lookforlog.protocol.logp.LogP;
import com.lookforlog.log.subscribe.SubscriberManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CancelSubscribeHandler extends BasicAuthRequestHandler<PathRequest> {
    private final static Logger logger = LoggerFactory.getLogger(CancelSubscribeHandler.class);
    private final SubscriberManager subscriberManager;

    @Autowired
    public CancelSubscribeHandler(SubscriberManager subscriberManager) {
        super(PathRequest.class);
        this.subscriberManager = subscriberManager;
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, PathRequest request) throws Exception {
        File log = new File(request.getPath());
        subscriberManager.remove(ctx, log);
        logger.debug("cancel subscribe {} from {}", log.getAbsolutePath(), ctx.channel().remoteAddress());
    }
}
