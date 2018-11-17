package com.log.socket.logp.constants;

import java.util.Arrays;

public enum Request {
    INIT(0x1),
    SUBSCRIBE(0x2),
    CANCEL_SUBSCRIBE(0x3),
    REQUEST_BETWEEN(0x4),
    CHANGE_DIR(0x5),;

    private int flag;

    Request(int flag) {
        this.flag = flag;
    }

    public static Request valueOf(int flag) {
        return Arrays.stream(Request.values()).filter(r -> r.flag == flag).findFirst().orElse(null);
    }

    public int getFlag() {
        return flag;
    }
}
