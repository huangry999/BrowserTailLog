package com.log.socket.logp.head;

import com.log.socket.constants.Mode;

public class FrameHead {
    /**
     * size of frame head, byte
     */
    public final static int SIZE = 10;

    private StartFlag startFlag;
    private Size size;
    private Version version;
    private Level level;
    private ControlSignal controlSignal;
    private Mode mode;
    private Checksum checksum;

    public StartFlag getStartFlag() {
        return startFlag;
    }

    public void setStartFlag(StartFlag startFlag) {
        this.startFlag = startFlag;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public ControlSignal getControlSignal() {
        return controlSignal;
    }

    public void setControlSignal(ControlSignal controlSignal) {
        this.controlSignal = controlSignal;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }

    public Checksum getChecksum() {
        return checksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrameHead head = (FrameHead) o;

        if (startFlag != null ? !startFlag.equals(head.startFlag) : head.startFlag != null) return false;
        if (version != null ? !version.equals(head.version) : head.version != null) return false;
        if (controlSignal != null ? !controlSignal.equals(head.controlSignal) : head.controlSignal != null)
            return false;
        if (mode != head.mode) return false;
        if (level != null ? !level.equals(head.level) : head.level != null) return false;
        return size != null ? size.equals(head.size) : head.size == null;
    }

    @Override
    public int hashCode() {
        int result = startFlag != null ? startFlag.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (controlSignal != null ? controlSignal.hashCode() : 0);
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FrameHead{" +
                "startFlag=" + startFlag +
                ", version=" + version +
                ", controlSignal=" + controlSignal +
                ", mode=" + mode +
                ", level=" + level +
                ", size=" + size +
                ", checksum=" + checksum +
                '}';
    }
}
