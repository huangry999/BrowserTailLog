package com.log.uiapi.service.bean;

import com.log.fileservice.grpc.FileContext;
import com.log.fileservice.grpc.FileType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

@Getter
@Setter
public class LogFileAttribute implements Comparable<LogFileAttribute> {
    private String path;
    private String modifyUtcTime;
    private String name;
    private int type;
    private Long size;
    private String key;

    public LogFileAttribute(FileContext src) {
        this.type = src.getTypeValue();
        this.path = src.getFilePath();
        this.modifyUtcTime = DateFormatUtils.format(src.getModifyTime(), "yyyy-MM-dd'T'HH:mm'Z'");
        if (src.getType() == FileType.FILE) {
            this.size = src.getSize();
        }
        this.name = FilenameUtils.getName(src.getFilePath());
        this.key = src.getFilePath().hashCode() + "";
    }

    @Override
    public int compareTo(LogFileAttribute o) {
        if (this.type == o.type) {
            return this.name.compareTo(o.name);
        }
        return this.type == FileType.DIRECTORY.getNumber() ? -1 : 1;
    }
}
