package com.log.service.handler.requestbetween;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.log.service.bean.LogLineText;
import com.log.service.handler.BasicRequestHandler;
import com.log.service.handler.CommonRequest;
import com.log.socket.constants.Respond;
import com.log.socket.logp.LogP;
import com.log.socket.logp.LogPFactory;
import com.log.util.FileUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RequestBetweenHandler extends BasicRequestHandler {
    private final static Logger logger = LoggerFactory.getLogger(RequestBetweenHandler.class);
    @Value("${log.windowSize}")
    private int windowSize;

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, CommonRequest request) throws Exception {
        BetweenRequest betweenRequest = (BetweenRequest) request;
        File log = new File(request.getPath());
        if (!log.exists()) {
            return;
        }
        long t0 = betweenRequest.getTake() == null ? windowSize : betweenRequest.getTake();
        if (t0 < 0) {
            throw new IllegalArgumentException("Take value must be positive, actual: " + t0);
        }
        long from0 = betweenRequest.getFrom() - (t0 - 1);
        long skip = from0 > 0 ? from0 : 0;
        long take0 = betweenRequest.getFrom() - skip;
        logger.debug("{} request log {} skip {} take {}",
                ctx.channel().remoteAddress(),
                log.getAbsolutePath(),
                skip,
                take0);
        List<LogLineText> contents = FileUtils.getLogText(log, skip, take0);
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

    @Override
    protected CommonRequest parseBody(String body) throws Exception {
        if (Strings.isBlank(body)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(body, BetweenRequest.class);
    }
}

/**
 * Get log context by line number request.
 * Due to log showed by line number desc, the "from" parameter means the last line number of contents to take,
 * and the "take" parameter means the log row (include "from") count to take upside "from".
 */
class BetweenRequest extends CommonRequest {
    private int from;
    private Integer take;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        if (from <= 0)
            this.from = 0;
        else
            this.from = from;
    }

    public Integer getTake() {
        return take;
    }

    public void setTake(Integer take) {
        this.take = take;
    }
}
