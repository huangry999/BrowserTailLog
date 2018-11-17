package com.log.socket.logp.head;

public class DataPackageSize {
    /**
     * size of data package size, byte
     */
    public final static int SIZE = 2;
    private short value;

    public DataPackageSize(short value) {
        this.value = value;
    }

    public short getValue() {

        return value;
    }

    @Override
    public String toString() {
        return "DataPackageSize{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataPackageSize size = (DataPackageSize) o;

        return value == size.value;
    }

    @Override
    public int hashCode() {
        return (int) value;
    }
}
