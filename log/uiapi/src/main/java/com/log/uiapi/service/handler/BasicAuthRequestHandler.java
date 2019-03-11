package com.log.uiapi.service.handler;

import com.log.uiapi.protocol.constants.RespondStatus;
import com.log.uiapi.protocol.exception.LogPException;
import com.log.uiapi.protocol.logp.LogP;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;

public abstract class BasicAuthRequestHandler<T extends RequestParam> extends BasicRequestHandler<T> {
    @Value("${security.auth:''}")
    private String systemPassword;

    public BasicAuthRequestHandler(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public void authorization(ChannelHandlerContext ctx, LogP msg) throws Exception {
        Boolean authenticated = ctx.channel().attr(HandlerConstants.ATTR_AUTHENTICATED).get();
        if (authenticated != Boolean.TRUE && !Strings.isEmpty(systemPassword)) {
            throw new LogPException(RespondStatus.UNAUTHORIZED, "Required login");
        }
    }
}