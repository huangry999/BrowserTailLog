package com.log.socket.logp.head;

import com.log.socket.constants.Mode;
import com.log.socket.constants.Request;
import com.log.socket.constants.Respond;
import com.log.socket.constants.Sender;

public class FrameHead {
    /**
     * size of frame head, byte
     */
    public final static int SIZE = 10;

    private StartFlag startFlag;
    private Size size;
    private Version version;
    private Sender sender;
    private Respond respond;
    private Request request;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrameHead head = (FrameHead) o;

        if (!startFlag.equals(head.startFlag)) return false;
        if (!size.equals(head.size)) return false;
        if (!version.equals(head.version)) return false;
        if (sender != head.sender) return false;
        if (respond != head.respond) return false;
        if (request != head.request) return false;
        return mode == head.mode;
    }

    @Override
    public int hashCode() {
        int result = startFlag.hashCode();
        result = 31 * result + size.hashCode();
        result = 31 * result + version.hashCode();
        result = 31 * result + sender.hashCode();
        result = 31 * result + (respond != null ? respond.hashCode() : 0);
        result = 31 * result + (request != null ? request.hashCode() : 0);
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        return result;
    }

    public Respond getRespond() {
        return respond;
    }

    public void setRespond(Respond respond) {
        this.respond = respond;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
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

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "FrameHead{" +
                "startFlag=" + startFlag +
                ", size=" + size +
                ", version=" + version +
                ", sender=" + sender +
                ", respond=" + respond +
                ", request=" + request +
                ", mode=" + mode +
                ", checksum=" + checksum +
                '}';
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
