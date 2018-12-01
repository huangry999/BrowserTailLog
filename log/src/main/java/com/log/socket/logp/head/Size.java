package com.log.socket.logp.head;

public class Size {
    /**
     * size of data package size, byte
     */
    public final static int SIZE = 2;
    private short value;

    public Size(short value) {
        this.value = value;
    }

    public short getValue() {

        return value;
    }

    @Override
    public String toString() {
        return "Size{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Size size = (Size) o;

        return value == size.value;
    }

    @Override
    public int hashCode() {
        return (int) value;
    }
}
