package com.log.service.handler.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.log.service.handler.BasicRequestHandler;
import com.log.service.handler.HandlerConstants;
import com.log.service.handler.RequestParam;
import com.log.protocol.constants.Respond;
import com.log.protocol.logp.LogP;
import com.log.protocol.logp.LogPFactory;
import com.log.protocol.util.PrintUtils;
import com.log.protocol.constants.RespondStatus;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.Date;

/**
 * Login handler, will record login info
 */
@Component
public class LoginHandler extends BasicRequestHandler<LoginRequest> {
    private final static Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final static String SALT = "34)8e$";
    @Value("${security.auth}")
    private String systemPassword;
    @Value("${security.expireMinutes}")
    private Integer expireMinutes;

    public LoginHandler() {
        super(LoginRequest.class);
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, LoginRequest request) throws Exception {
        if (!Strings.isEmpty(systemPassword)) {
            if (request.getPassword().isEmpty()) {
                LogP respond = LogPFactory.defaultInstance0()
                        .setRespond(Respond.LOGIN)
                        .create0(RespondStatus.UNAUTHORIZED, "Empty Password");
                ctx.writeAndFlush(respond);
                logger.debug("{} login fail with empty password: {}", ctx.channel().remoteAddress());
            }
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String input = request.getPassword();
            final String p = request.getId() + SALT + systemPassword;
            messageDigest.update(p.getBytes());
            String encrypt = PrintUtils.toString0(messageDigest.digest(), "");
            if (input.equals(encrypt)) {
                logger.debug("{} login success", ctx.channel().remoteAddress());
                final Algorithm al = Algorithm.HMAC256(HandlerConstants.JWT_SECRETE_KEY);
                Date expire = null;
                if (this.expireMinutes != null) {
                    expire = DateUtils.addMinutes(new Date(), expireMinutes);
                }
                final String token = JWT.create()
                        .withIssuer(HandlerConstants.JWT_ISSUER)
                        .withSubject(request.getId())
                        .withExpiresAt(expire)
                        .sign(al);
                ctx.channel().attr(HandlerConstants.ATTR_AUTHENTICATED).set(true);
                LogP respond = LogPFactory.defaultInstance0()
                        .setRespond(Respond.LOGIN)
                        .addData("token", token)
                        .create();
                ctx.writeAndFlush(respond);
            } else {
                ctx.channel().attr(HandlerConstants.ATTR_AUTHENTICATED).set(false);
                LogP respond = LogPFactory.defaultInstance0()
                        .setRespond(Respond.LOGIN)
                        .create0(RespondStatus.UNAUTHORIZED, "Password error");
                ctx.writeAndFlush(respond);
                logger.debug("{} login fail with wrong password", ctx.channel().remoteAddress());
            }
        }
    }
}

/**
 * User login id and password bean
 */
class LoginRequest implements RequestParam {
    private String id;
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


