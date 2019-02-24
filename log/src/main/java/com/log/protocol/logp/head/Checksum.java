package com.log.protocol.logp.head;

public class Checksum {
    /**
     * size of checksum, byte
     */
    public static final int SIZE = 2;
    private short value;

    public Checksum(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Checksum{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Checksum checksum = (Checksum) o;

        return value == checksum.value;
    }

    @Override
    public int hashCode() {
        return (int) value;
    }
}
