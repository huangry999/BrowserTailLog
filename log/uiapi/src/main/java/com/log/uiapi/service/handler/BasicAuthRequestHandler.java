package com.log.uiapi.service.handler;

import com.log.common.spring.SpringUtils;
import com.log.uiapi.protocol.constants.RespondStatus;
import com.log.uiapi.protocol.exception.LogPException;
import com.log.uiapi.protocol.logp.LogP;
import com.log.uiapi.security.AuthorizationServerConfig;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;

public abstract class BasicAuthRequestHandler<T extends RequestParam> extends BasicRequestHandler<T> {
    public BasicAuthRequestHandler(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public void authorization(ChannelHandlerContext ctx, LogP msg) throws Exception {
        String token = ctx.channel().attr(HandlerConstants.ATTR_TOKEN).get();
        if (Strings.isBlank(token)) {
            throw new LogPException(RespondStatus.UNAUTHORIZED, "Required token");
        }
        try {
            SpringUtils.get(AuthorizationServerConfig.class).getServices().loadAuthentication(token);
        } catch (InvalidTokenException | AuthenticationException e) {
            throw new LogPException(RespondStatus.UNAUTHORIZED, e.getLocalizedMessage());
        }
    }
}
