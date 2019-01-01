package com.log.service.handler.init;

import com.log.config.LogFileProperties;
import com.log.service.LogFileService;
import com.log.service.handler.BasicRequestHandler;
import com.log.service.handler.CommonRequest;
import com.log.socket.constants.Respond;
import com.log.socket.logp.LogP;
import com.log.socket.logp.LogPFactory;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The first handler sent from client.
 * Will respond the environment parameters.
 */
@Component
public class InitHandler extends BasicRequestHandler {
    private final LogFileService logFileService;
    private final LogFileProperties logFileProperties;
    private final static Logger logger = LoggerFactory.getLogger(InitHandler.class);

    @Autowired
    public InitHandler(LogFileService logFileService, LogFileProperties logFileProperties) {
        this.logFileService = logFileService;
        this.logFileProperties = logFileProperties;
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, CommonRequest request) throws Exception {
        LogPFactory respond = LogPFactory.defaultInstance0()
                .setRespond(Respond.INIT);
        logger.debug("new connect from {}, init", ctx.channel().remoteAddress());
        ctx.writeAndFlush(respond.create()).addListener(future -> {
            if (future.isSuccess()) {
                logger.debug("send successfully");
            } else {
                throw new RuntimeException("writeAndFlush error");
            }
        });
    }
}
