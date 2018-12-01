package com.log.service.handler.init;

import com.log.config.LogFileProperties;
import com.log.service.LogFileService;
import com.log.service.bean.LogFileAttribute;
import com.log.service.handler.BasicRequestHandler;
import com.log.service.handler.CommonRequest;
import com.log.socket.logp.LogP;
import com.log.socket.logp.LogPFactory;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The first handler sent from client.
 * Will respond the environment parameters and files under the root log directory.
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
        List<LogFileAttribute> rootDir = logFileProperties.getPath()
                .stream()
                .map(this.logFileService::convert)
                .collect(Collectors.toList());
        LogP respond = LogPFactory.defaultInstance0().addData("data", rootDir).create();
        logger.debug("new connect from {}, init", ctx.channel().remoteAddress());
        ctx.writeAndFlush(respond).addListener(future -> {
            if (future.isSuccess()) {
                logger.debug("send successfully");
            } else {
                throw new RuntimeException("writeAndFlush error");
            }
        });
    }
}
