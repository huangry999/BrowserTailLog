package com.log.service.handler.cd;

import com.fasterxml.jackson.core.JsonProcessingException;
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

/**
 * The handler of change directory handler from client.
 * Will respond files under the directory.
 */
@Component
public class ChangeDirectoryHandler extends BasicRequestHandler {
    private final LogFileService logFileService;
    private final static Logger logger = LoggerFactory.getLogger(ChangeDirectoryHandler.class);

    @Autowired
    public ChangeDirectoryHandler(LogFileService logFileService) {
        this.logFileService = logFileService;
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, CommonRequest request) throws JsonProcessingException {
        List<LogFileAttribute> result = this.logFileService.listLogFiles(request.getPath(), false);
        LogP respond = LogPFactory.defaultInstance0().addData("data", result).create();
        logger.debug("{} change directory to {}", ctx.channel().remoteAddress(), request.getPath());
        ctx.writeAndFlush(respond).addListener(future -> {
            if (future.isSuccess()) {
                logger.debug("send successfully");
            } else {
                throw new RuntimeException("writeAndFlush error");
            }
        });
    }
}
