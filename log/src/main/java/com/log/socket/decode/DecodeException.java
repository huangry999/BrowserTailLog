package com.log.socket.decode;

public class DecodeException extends RuntimeException {

    public DecodeException(String msg){
        super(msg);
    }

    public DecodeException(String msg, Exception e){
        super(msg, e);
    }
}
