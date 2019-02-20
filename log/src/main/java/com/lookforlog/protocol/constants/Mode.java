package com.lookforlog.protocol.constants;

import java.util.Arrays;

public enum Mode {
    NONE(0x0),
    DELETE(0x1),
    CREATE(0x2),
    MODIFY(0x3),;

    /**
     * size of mode, byte
     */
    public static final int SIZE = 1;
    private int flag;

    Mode(int flag) {
        this.flag = flag;
    }

    public static Mode valueOf(int flag) {
        return Arrays.stream(Mode.values()).filter(m -> m.flag == flag).findFirst().orElse(null);
    }

    public int getFlag() {
        return flag;
    }
}
