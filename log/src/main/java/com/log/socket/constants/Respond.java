package com.log.socket.constants;

import com.log.constant.CodedConstant;

public enum Respond implements CodedConstant {
    NONE(0x0),
    INIT(0x1),
    LIST_FILE(0x2),
    NEW_LOG_CONTENT(0x3),
    LOG_CONTENT_BETWEEN(0x4),;
    private int code;

    /**
     * size of respond, byte
     */
    public static final int SIZE = 1;

    Respond(int code) {
        this.code = code;
    }

    public static Respond valueOf(int flag) {
        return CodedConstant.valueOf(flag, Respond.values(), Respond.NONE);
    }

    @Override
    public int getCode() {
        return code;
    }
}
