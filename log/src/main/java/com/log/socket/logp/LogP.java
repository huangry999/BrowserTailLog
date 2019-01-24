package com.log.socket.logp;

import com.log.socket.logp.head.FrameHead;

public class LogP {
    private FrameHead head;
    private String body;

    public void setHead(FrameHead head) {
        this.head = head;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public FrameHead getHead() {
        return head;
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogP logP = (LogP) o;

        if (head != null ? !head.equals(logP.head) : logP.head != null) return false;
        return body != null ? body.equals(logP.body) : logP.body == null;
    }

    @Override
    public int hashCode() {
        int result = head != null ? head.hashCode() : 0;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (body.length() > 100) {
            return "LogP{" +
                    "head=" + head +
                    ", body=" + body.substring(0, 100) +
                    "...}";
        } else {
            return "LogP{" +
                    "head=" + head +
                    ", body=" + body +
                    '}';
        }

    }
}
