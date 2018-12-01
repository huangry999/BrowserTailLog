package com.log.service.bean;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.util.Date;

public class LogFileAttribute {
    private String path;
    private String modifyUtcTime;
    private String name;
    private boolean isDir;

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
        result.isDir = file.isDirectory();
        result.path = file.getParentFile().toPath().toString();
        result.name = file.getName();
        Date md = new Date(file.lastModified());
        result.modifyUtcTime = DateFormatUtils.format(md, "yyyy-MM-dd'T'HH:mm'Z'");
        return result;
    }

    /**
     * Boolean of if is directory
     *
     * @return true if it's directory
     */
    public boolean isDir() {
        return isDir;
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
}
