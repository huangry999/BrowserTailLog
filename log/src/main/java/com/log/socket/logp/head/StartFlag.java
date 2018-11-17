package com.log.socket.logp.head;

import com.log.socket.decode.DecodeException;

public class StartFlag {
    /**
     * size of start flag, byte
     */
    public final static int SIZE = 2;

    /**
     * value of start flag
     */
    public final static short START_FLAG = 0x112c;

    private short value;

    public StartFlag(short value){
        this.value = value;
    }

    /**
     * check if the start flag is valid.
     *
     * @return self
     */
    public StartFlag check() {
        if (this.value != START_FLAG) {
            throw new DecodeException("Start flag is invalid: " + this.value);
        }
        return this;
    }

    public short getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "StartFlag{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StartFlag startFlag = (StartFlag) o;

        return value == startFlag.value;
    }

    @Override
    public int hashCode() {
        return (int) value;
    }
}
