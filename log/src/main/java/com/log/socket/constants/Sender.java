package com.log.socket.constants;

import com.log.constant.CodedConstant;

public enum Sender implements CodedConstant {
    SERVER(1),
    CLIENT(2),;

    private int code;

    /**
     * size of sender, byte
     */
    public static final int SIZE = 1;

    Sender(int code) {
        this.code = code;
    }

    public static Sender valueOf(Integer code) {
        return CodedConstant.valueOf(code, Sender.values(), Sender.SERVER);
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
