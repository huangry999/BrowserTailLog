package com.lookforlog.log.constant;

public enum FileType implements CodedConstant {
    DIRECTORY(1),
    LOG_FILE(2),;

    private int code;

    FileType(int code) {
        this.code = code;
    }


    @Override
    public int getCode() {
        return this.code;
    }
}
