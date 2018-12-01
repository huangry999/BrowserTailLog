package com.log.socket.codec;

public class codecException extends RuntimeException {

    public codecException(String msg){
        super(msg);
    }

    public codecException(String msg, Exception e){
        super(msg, e);
    }
}
