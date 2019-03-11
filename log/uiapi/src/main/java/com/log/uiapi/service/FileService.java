package com.log.uiapi.service;

import com.log.uiapi.service.bean.LogLineText;

import java.io.File;
import java.util.List;

public interface FileService {

    List<LogLineText> read(File file, long skip, long take);

    long count(File file);
}
