package com.log.protocol.logp.head;

import com.log.protocol.constants.Respond;
import com.log.protocol.constants.Mode;
import com.log.protocol.constants.Request;
import com.log.protocol.constants.Sender;
import org.springframework.lang.NonNull;

public class FrameHead {
    public static final int SIZE = 10;
    @NonNull
    private StartFlag startFlag;
    private Size size;
    @NonNull
    private Version version;
    @NonNull
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

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrameHead frameHead = (FrameHead) o;

        if (!startFlag.equals(frameHead.startFlag)) return false;
        if (size != null ? !size.equals(frameHead.size) : frameHead.size != null) return false;
        if (!version.equals(frameHead.version)) return false;
        if (sender != frameHead.sender) return false;
        if (respond != frameHead.respond) return false;
        if (request != frameHead.request) return false;
        return mode == frameHead.mode;
    }

    @Override
    public int hashCode() {
        int result = startFlag.hashCode();
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + version.hashCode();
        result = 31 * result + sender.hashCode();
        result = 31 * result + (respond != null ? respond.hashCode() : 0);
        result = 31 * result + (request != null ? request.hashCode() : 0);
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        return result;
    }
}
