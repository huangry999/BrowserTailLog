package com.log.service;

import org.springframework.stereotype.Service;

import java.io.File;

public interface LogFileService {
    /**
     * bind a log file
     * @param log target log
     * @return bind result
     */
    boolean bind(File log);

}
