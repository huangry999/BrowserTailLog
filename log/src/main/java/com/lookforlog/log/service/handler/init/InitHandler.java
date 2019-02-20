package com.lookforlog.log.service.handler.init;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lookforlog.log.config.LogFileProperties;
import com.lookforlog.log.service.LogFileService;
import com.lookforlog.log.service.handler.BasicRequestHandler;
import com.lookforlog.log.service.handler.HandlerConstants;
import com.lookforlog.log.service.handler.RequestParam;
import com.lookforlog.protocol.constants.Respond;
import com.lookforlog.protocol.logp.LogP;
import com.lookforlog.protocol.logp.LogPFactory;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The handler for first request sent from client.
 * Will respond the environment parameters.
 */
@Component
public class InitHandler extends BasicRequestHandler<InitRequest> {
    private final LogFileService logFileService;
    private final LogFileProperties logFileProperties;
    private final static Logger logger = LoggerFactory.getLogger(InitHandler.class);
    @Value("${log.windowSize}")
    private int windowSize;
    @Value("${log.shrinkThreshold}")
    private Integer shrinkThreshold;

    @Autowired
    public InitHandler(LogFileService logFileService, LogFileProperties logFileProperties) {
        super(InitRequest.class);
        this.logFileService = logFileService;
        this.logFileProperties = logFileProperties;
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, InitRequest request) throws Exception {
        LogPFactory respond = LogPFactory.defaultInstance0()
                .addData("windowSize", windowSize)
                .addData("shrinkThreshold", shrinkThreshold)
                .setRespond(Respond.INIT);
        logger.debug("new connect from {}, init", ctx.channel().remoteAddress());
        if (Strings.isNotBlank(request.getToken()) && ctx.channel().attr(HandlerConstants.ATTR_AUTHENTICATED).get() != Boolean.TRUE) {
            try {
                Algorithm algorithm = Algorithm.HMAC256(HandlerConstants.JWT_SECRETE_KEY);
                JWTVerifier verifier = JWT.require(algorithm).build();
                verifier.verify(request.getToken());
                ctx.channel().attr(HandlerConstants.ATTR_AUTHENTICATED).set(true);
                logger.debug("{} login with token", ctx.channel().remoteAddress());
            } catch (JWTVerificationException e) {
                ctx.channel().attr(HandlerConstants.ATTR_AUTHENTICATED).set(false);
            } catch (Exception e) {
                logger.error("JWT verify error.", e);
            }
        }
        ctx.writeAndFlush(respond.create());
    }
}

class InitRequest implements RequestParam {
    private String id;
    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


