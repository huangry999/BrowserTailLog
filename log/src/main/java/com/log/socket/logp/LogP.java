package com.log.socket.logp;

import com.log.socket.logp.head.FrameHead;

public class LogP {
    private FrameHead head;
    private Object body;

    public void setHead(FrameHead head) {
        this.head = head;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public FrameHead getHead() {
        return head;
    }

    public Object getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "LogP{" +
                "head=" + head +
                ", body=" + body +
                '}';
    }
}
