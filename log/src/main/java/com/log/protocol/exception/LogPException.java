package com.log.protocol.exception;

import com.log.protocol.constants.RespondStatus;

public class LogPException extends RuntimeException {
    final RespondStatus respondStatus;

    public LogPException(RespondStatus respondStatus, String message) {
        super(message);
        this.respondStatus = respondStatus;
    }

    public LogPException(RespondStatus respondStatus, String message, Throwable cause) {
        super(message, cause);
        this.respondStatus = respondStatus;
    }

    public RespondStatus getRespondStatus() {
        return respondStatus;
    }
}
