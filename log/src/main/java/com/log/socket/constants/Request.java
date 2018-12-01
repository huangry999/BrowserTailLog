package com.log.socket.constants;

import com.log.service.handler.RequestHandler;
import com.log.service.handler.cancelsubscribe.CancelSubscribeHandler;
import com.log.service.handler.cd.ChangeDirectoryHandler;
import com.log.service.handler.init.InitHandler;
import com.log.service.handler.requestbetween.RequestBetweenHandler;
import com.log.service.handler.subscribe.SubscribeHandler;
import com.log.util.SpringUtils;

import java.util.Arrays;

public enum Request {
    NONE(0x0, null),
    INIT(0x1, InitHandler.class),
    SUBSCRIBE(0x2, SubscribeHandler.class),
    CANCEL_SUBSCRIBE(0x3, CancelSubscribeHandler.class),
    REQUEST_BETWEEN(0x4, RequestBetweenHandler.class),
    CHANGE_DIR(0x5, ChangeDirectoryHandler.class),;

    private int flag;
    private Class<? extends RequestHandler> handleClass;

    Request(int flag, Class<? extends RequestHandler> handleClass) {
        this.flag = flag;
        this.handleClass = handleClass;
    }

    public static Request valueOf(int flag) {
        return Arrays.stream(Request.values()).filter(r -> r.flag == flag).findFirst().orElse(null);
    }

    public RequestHandler handler() {
        return SpringUtils.get(this.handleClass);
    }

    public int getFlag() {
        return flag;
    }
}
