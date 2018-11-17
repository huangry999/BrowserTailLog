package com.log.socket.logp.head;

public class Level {
    /**
     * size of level, byte
     */
    public static final int SIZE = 1;

    private short total;
    private short current;

    public short getTotal() {
        return total;
    }

    public void setTotal(short total) {
        this.total = total;
    }

    public short getCurrent() {
        return current;
    }

    public void setCurrent(short current) {
        this.current = current;
    }

    @Override
    public String toString() {
        return "Level{" +
                "total=" + total +
                ", current=" + current +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Level level = (Level) o;

        if (total != level.total) return false;
        return current == level.current;
    }

    @Override
    public int hashCode() {
        int result = (int) total;
        result = 31 * result + (int) current;
        return result;
    }
}
