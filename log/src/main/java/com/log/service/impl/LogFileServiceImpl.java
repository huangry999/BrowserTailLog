package com.log.service.impl;

import com.log.logmonitor.LogMonitor;
import com.log.service.LogFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.File;

@Service
public class LogFileServiceImpl implements LogFileService {
    private static final Logger logger = LoggerFactory.getLogger(LogFileServiceImpl.class);

    @Override
    public boolean bind(@NotNull File log) {
        return false;
    }
}
