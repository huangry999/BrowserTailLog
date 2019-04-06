package com.log.fileservice.monitor.monitor;

import com.log.fileservice.grpc.EventType;
import com.log.fileservice.grpc.FileType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.File;

@Getter
public class FileEvent extends ApplicationEvent {
    private FileType fileType;
    private File file;
    private EventType eventType;

    public FileEvent(FileType fileType, File file, EventType eventType) {
        super(file);
        this.fileType = fileType;
        this.file = file;
        this.eventType = eventType;
    }
}
