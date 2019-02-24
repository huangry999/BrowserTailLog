package com.log.service.handler.requestbetween;

import com.log.logreader.LogReader;
import com.log.service.bean.LogLineText;
import com.log.service.handler.BasicAuthRequestHandler;
import com.log.service.handler.PathRequest;
import com.log.protocol.constants.Respond;
import com.log.protocol.logp.LogP;
import com.log.protocol.logp.LogPFactory;
import io.netty.channel.ChannelHandlerContext;
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
    @Value("${log.windowSize}")
    private int windowSize;
    private final LogReader logReader;

    @Autowired
    public RequestBetweenHandler(LogReader logReader) {
        super(BetweenRequest.class);
        this.logReader = logReader;
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, BetweenRequest request) throws Exception {
        File log = new File(request.getPath());
        if (!log.exists()) {
            return;
        }
        long skip = request.getSkip();
        int take = request.getTake() == null ? windowSize : request.getTake();
        if (skip < 0 || take < 0) {
            throw new IllegalArgumentException(String.format("Take %s or Skip %s value is negative", take, skip));
        }
        logger.debug("{} request log {} skip {} take {}",
                ctx.channel().remoteAddress(),
                log.getAbsolutePath(),
                skip,
                take);
        List<LogLineText> contents = logReader.read(log, skip, take);
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
class BetweenRequest extends PathRequest {
    private long skip;
    private Integer take;

    public long getSkip() {
        return skip;
    }

    public void setSkip(long skip) {
        this.skip = skip;
    }

    public Integer getTake() {
        return take;
    }

    public void setTake(Integer take) {
        this.take = take;
    }
}
