package com.lookforlog.protocol.constants;

import com.lookforlog.log.constant.CodedConstant;
import com.lookforlog.log.service.handler.RequestHandler;
import com.lookforlog.log.service.handler.cancelsubscribe.CancelSubscribeHandler;
import com.lookforlog.log.service.handler.cd.ChangeDirectoryHandler;
import com.lookforlog.log.service.handler.init.InitHandler;
import com.lookforlog.log.service.handler.login.LoginHandler;
import com.lookforlog.log.service.handler.requestbetween.RequestBetweenHandler;
import com.lookforlog.log.service.handler.subscribe.SubscribeHandler;
import com.lookforlog.util.SpringUtils;

public enum Request implements CodedConstant {
    NONE(0x0, null),
    INIT(0x1, InitHandler.class),
    SUBSCRIBE(0x2, SubscribeHandler.class),
    CANCEL_SUBSCRIBE(0x3, CancelSubscribeHandler.class),
    REQUEST_BETWEEN(0x4, RequestBetweenHandler.class),
    CHANGE_DIR(0x5, ChangeDirectoryHandler.class),
    LOGIN(0x6, LoginHandler.class),;

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
