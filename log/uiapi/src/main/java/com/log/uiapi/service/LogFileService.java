package com.log.uiapi.service;

import com.log.uiapi.service.bean.LogFileAttribute;

import java.io.File;
import java.util.List;

public interface LogFileService {
    /**
     * bind a log file
     *
     * @param log target log
     * @return bind result
     */
    boolean bind(File log);

    /**
     * List log files under the directory, will filter by system config except recursive.
     *
     * @param directory directory path
     * @param isRecursive include child directories
     * @return files
     */
    List<LogFileAttribute> listLogFiles(String directory, boolean isRecursive);

    /**
     * List root files, defined in configuration
     *
     * @return root files
     */
    List<LogFileAttribute> listRoot();

    /**
     * Convert by file path
     *
     * @param file the file path
     * @return attributes
     */
    LogFileAttribute convert(String file);
}
