package com.log.uiapi.protocol.constants;

import com.log.common.constant.CodedConstant;
import com.log.uiapi.service.handler.RequestHandler;
import com.log.uiapi.service.handler.cancelsubscribe.CancelSubscribeHandler;
import com.log.uiapi.service.handler.cd.ChangeDirectoryHandler;
import com.log.uiapi.service.handler.requestbetween.RequestBetweenHandler;
import com.log.uiapi.service.handler.subscribe.SubscribeHandler;
import com.log.uiapi.service.handler.token.TokenHandler;
import com.log.uiapi.spring.SpringUtils;

public enum Request implements CodedConstant {
    NONE(0x0, null),
    TOKEN(0x1, TokenHandler.class),
    SUBSCRIBE(0x2, SubscribeHandler.class),
    CANCEL_SUBSCRIBE(0x3, CancelSubscribeHandler.class),
    REQUEST_BETWEEN(0x4, RequestBetweenHandler.class),
    CHANGE_DIR(0x5, ChangeDirectoryHandler.class),
    ;

    private int code;
    private Class<? extends RequestHandler> handleClass;

    /**
     * size of request, byte
     */
    public static final int SIZE = 1;

    Request(int code, Class<? extends RequestHandler> handleClass) {
        this.code = code;
        this.handleClass = handleClass;
    }

    public static Request valueOf(Integer code) {
        return CodedConstant.valueOf(code, Request.values(), Request.NONE);
    }

    public RequestHandler handler() {
        return SpringUtils.get(this.handleClass);
    }

    @Override
    public int getCode() {
        return code;
    }
}
