package com.log.service.bean;

import com.log.constant.CodedConstant;
import com.log.constant.FileType;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.util.Date;

public class LogFileAttribute implements Comparable<LogFileAttribute> {
    private String path;
    private String modifyUtcTime;
    private String name;
    private int type;
    private Long size;
    private String key;

    /**
     * Parse by a file
     *
     * @param file the file
     * @return file attribute
     */
    public static LogFileAttribute valueOf(File file) {
        LogFileAttribute result = new LogFileAttribute();
        if (!file.exists()) {
            return result;
        }
        result.type = (file.isDirectory() ? FileType.DIRECTORY : FileType.LOG_FILE).getCode();
        result.path = file.toPath().toString();
        result.name = file.getName();
        result.key = file.hashCode() + "";
        Date md = new Date(file.lastModified());
        result.modifyUtcTime = DateFormatUtils.format(md, "yyyy-MM-dd'T'HH:mm'Z'");
        if (!file.isDirectory()) {
            result.size = file.length();
        }
        return result;
    }

    public int getType() {
        return type;
    }

    /**
     * Get the path of file, exclude file name
     *
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the file modify time in utc.
     *
     * @return time
     */
    public String getModifyUtcTime() {
        return modifyUtcTime;
    }

    /**
     * Get the file name
     *
     * @return file name
     */
    public String getName() {
        return name;
    }

    public Long getSize() {
        return size;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int compareTo(LogFileAttribute o) {
        if (this.type == o.type) {
            return this.name.compareTo(o.name);
        }
        return CodedConstant.valueOf(this.type, FileType.values(), null) == FileType.DIRECTORY ? -1 : 1;
    }
}
