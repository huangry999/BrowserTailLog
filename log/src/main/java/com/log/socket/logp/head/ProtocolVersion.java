package com.log.socket.logp.head;

public class ProtocolVersion {
    /**
     * size of protocol version, byte
     */
    public final static int SIZE = 1;
    private short mainVersion;
    private short subVersion;

    public short getMainVersion() {
        return mainVersion;
    }

    public void setMainVersion(short mainVersion) {
        this.mainVersion = mainVersion;
    }

    public short getSubVersion() {
        return subVersion;
    }

    public void setSubVersion(short subVersion) {
        this.subVersion = subVersion;
    }

    @Override
    public String toString() {
        return "ProtocolVersion{" +
                "mainVersion=" + mainVersion +
                ", subVersion=" + subVersion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProtocolVersion version = (ProtocolVersion) o;

        if (mainVersion != version.mainVersion) return false;
        return subVersion == version.subVersion;
    }

    @Override
    public int hashCode() {
        int result = (int) mainVersion;
        result = 31 * result + (int) subVersion;
        return result;
    }
}
