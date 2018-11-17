package com.log.socket.logp.constants;

public enum Sender {
    SERVER(false),
    CLIENT(true),;

    private boolean flag;

    Sender(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return flag;
    }

    public static Sender valueOf(boolean flag) {
        return flag ? CLIENT : SERVER;
    }
}
