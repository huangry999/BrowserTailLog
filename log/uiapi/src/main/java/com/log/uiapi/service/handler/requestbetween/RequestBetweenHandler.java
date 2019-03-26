package com.log.uiapi.service.handler.requestbetween;

import com.log.uiapi.protocol.constants.Respond;
import com.log.uiapi.protocol.logp.LogP;
import com.log.uiapi.protocol.logp.LogPFactory;
import com.log.uiapi.service.FileService;
import com.log.uiapi.service.bean.LogLineText;
import com.log.uiapi.service.handler.BasicAuthRequestHandler;
import com.log.uiapi.service.handler.PathRequest;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class RequestBetweenHandler extends BasicAuthRequestHandler<BetweenRequest> {
    private final static Logger logger = LoggerFactory.getLogger(RequestBetweenHandler.class);
    @Value("${uiapi-properties.window-size}")
    private int windowSize;
    private final FileService fileService;

    @Autowired
    public RequestBetweenHandler(FileService fileService) {
        super(BetweenRequest.class);
        this.fileService = fileService;
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, BetweenRequest request) throws Exception {
        File log = new File(request.getPath());
        if (!log.exists()) {
            return;
        }
        long skip = request.getSkip();
        long take = request.getTake() == null ? windowSize : request.getTake();
        if (skip < 0 || take < 0) {
            throw new IllegalArgumentException(String.format("Take %s or Skip %s value is negative", take, skip));
        }
        logger.debug("{} request log {} skip {} take {}",
                ctx.channel().remoteAddress(),
                log.getAbsolutePath(),
                skip,
                take);
        List<LogLineText> contents = fileService.read(request.getHostName(), request.getPath(), skip, take);
        LogP respond = LogPFactory.defaultInstance0()
                .addData("data", contents)
                .addData("path", request.getPath())
                .setRespond(Respond.LOG_CONTENT_BETWEEN)
                .create();
        ctx.writeAndFlush(respond).addListener(future -> {
            if (future.isSuccess()) {
                logger.debug("send successfully");
            } else {
                throw new RuntimeException("writeAndFlush error");
            }
        });
    }
}

/**
 * Get log context by line number request.
 * Due to log showed by line number desc, the "from" parameter means the last line number of contents to take,
 * and the "take" parameter means the log row (include "from") count to take upside "from".
 */
@Setter
@Getter
class BetweenRequest extends PathRequest {
    private long skip;
    private Long take;
    private String hostName;
}
