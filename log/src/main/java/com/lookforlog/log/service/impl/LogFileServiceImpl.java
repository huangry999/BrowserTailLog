package com.lookforlog.log.service.impl;

import com.lookforlog.log.config.LogFileProperties;
import com.lookforlog.log.service.LogFileService;
import com.lookforlog.log.service.bean.LogFileAttribute;
import com.lookforlog.util.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogFileServiceImpl implements LogFileService {
    private static final Logger logger = LoggerFactory.getLogger(LogFileServiceImpl.class);
    private final LogFileProperties logFileProperties;

    @Autowired
    public LogFileServiceImpl(LogFileProperties logFileProperties) {
        this.logFileProperties = logFileProperties;
    }

    @Override
    public boolean bind(@NotNull File log) {
        return false;
    }

    @Override
    public List<LogFileAttribute> listLogFiles(String directory, boolean isRecursive) {
        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            return new ArrayList<>();
        }
        return FileUtils.listFiles(dir, isRecursive, new SuffixFileFilter(logFileProperties.getSuffix()))
                .stream()
                .map(LogFileAttribute::valueOf)
                .collect(Collectors.toList());

    }

    @Override
    public List<LogFileAttribute> listRoot() {
        return logFileProperties.getPath()
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public LogFileAttribute convert(String file) {
        return LogFileAttribute.valueOf(new File(file));
    }
}
