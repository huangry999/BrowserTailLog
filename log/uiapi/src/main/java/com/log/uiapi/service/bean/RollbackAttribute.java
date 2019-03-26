package com.log.uiapi.service.bean;

import com.log.fileservice.grpc.DirectoryContextRespond;
import com.log.fileservice.grpc.DirectoryLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RollbackAttribute {
    private boolean inHostPath;
    private boolean inRootPath;
    private String rollBackPath;

    public RollbackAttribute(DirectoryContextRespond src) {
        this.inHostPath = src.getLevel() == DirectoryLevel.HOST;
        this.inRootPath = src.getLevel() == DirectoryLevel.ROOT;
        rollBackPath = src.getRollback();
    }

    public RollbackAttribute() {
    }
}
