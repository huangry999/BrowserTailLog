package com.log.service.handler.requestbetween;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.log.service.handler.BasicRequestHandler;
import com.log.service.handler.CommonRequest;
import com.log.socket.logp.LogP;
import com.log.socket.logp.LogPFactory;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RequestBetweenHandler extends BasicRequestHandler {
    private final static Logger logger = LoggerFactory.getLogger(RequestBetweenHandler.class);

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, CommonRequest request) throws Exception {
        BetweenRequest betweenRequest = (BetweenRequest) request;
        File log = new File(request.getPath());
        if (!log.exists()) {
            return;
        }
        logger.debug("{} request log {} from {} to {}",
                ctx.channel().remoteAddress(),
                log.getAbsolutePath(),
                betweenRequest.getFrom(),
                betweenRequest.getTo());
        Stream<String> stream = Files.lines(log.toPath()).skip(betweenRequest.getFrom() - 1);
        if (betweenRequest.getTo() != null) {
            stream = stream.limit(betweenRequest.getTo());
        }
        List<String> context = stream.collect(Collectors.toList());
        LogP respond = LogPFactory.defaultInstance0().addData("data", context).create();
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

class BetweenRequest extends CommonRequest {
    private int from;
    private Integer to;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        if (from <= 0)
            this.from = 0;
        else
            this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }
}
