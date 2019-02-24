package com.log.service.handler;

import io.netty.util.AttributeKey;

public interface HandlerConstants {
    AttributeKey<Boolean> ATTR_AUTHENTICATED = AttributeKey.valueOf("ATTR_AUTHENTICATED");
    String JWT_ISSUER = "LogSystem";
    String JWT_SECRETE_KEY = "sF^$df%34.";
}
