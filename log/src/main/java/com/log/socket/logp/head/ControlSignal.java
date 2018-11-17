package com.log.socket.logp.head;

import com.log.socket.logp.constants.Request;
import com.log.socket.logp.constants.Sender;

public class ControlSignal {
    /**
     * size of control signal, byte
     */
    public final static int SIZE = 1;

    private Sender sender;
    private Request request;

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
    public String toString() {
        return "ControlSignal{" +
                "sender=" + sender +
                ", request=" + request +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ControlSignal that = (ControlSignal) o;

        if (sender != that.sender) return false;
        return request == that.request;
    }

    @Override
    public int hashCode() {
        int result = sender != null ? sender.hashCode() : 0;
        result = 31 * result + (request != null ? request.hashCode() : 0);
        return result;
    }
}
