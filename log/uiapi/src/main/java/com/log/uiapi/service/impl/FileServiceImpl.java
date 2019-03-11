package com.log.uiapi.service.impl;

import com.log.uiapi.service.FileService;
import com.log.uiapi.service.bean.LogLineText;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public List<LogLineText> read(File file, long skip, long take) {
        return null;
    }

    @Override
    public long count(File file) {
        return 0;
    }
}
