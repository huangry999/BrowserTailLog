package com.lookforlog.protocol.constants;

import com.lookforlog.log.constant.CodedConstant;
import io.netty.handler.codec.http.HttpResponseStatus;

public enum RespondStatus implements CodedConstant {
    SUCCESS(0),
    UNAUTHORIZED(HttpResponseStatus.UNAUTHORIZED.code()),//401
    DECODE_ERROR(HttpResponseStatus.BAD_REQUEST.code()),//400
    INTERNAL_SERVER_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()),//500
    ;

    final int code;

    RespondStatus(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
