package com.log.util;

import com.log.config.LogFileProperties;
import com.log.service.bean.LogLineText;
import com.log.protocol.codec.LogProtocolCodec;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {
    /**
     * get file suffix
     *
     * @param file target file
     * @return the suffix, null if file is null, not a real file or there's no suffix
     */
    public static String getSuffix(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        int i = file.getName().lastIndexOf('.');
        if (i != -1) {
            return file.getName().substring(i + 1);
        }
        return null;
    }

    /**
     * list files of director
     *
     * @param directory  target directory
     * @param recursive  is recursive
     * @param fileFilter filter, nullable
     * @return the files of this directory
     */
    public static List<File> listFiles(File directory, boolean recursive, FileFilter fileFilter) {
        List<File> result = new ArrayList<>();
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return result;
        }
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                result.add(f);
                if (recursive) {
                    result.addAll(listFiles(f, true, fileFilter));
                }
            } else if (fileFilter == null || fileFilter.accept(f)) {
                result.add(f);
            }
        }
        return result;
    }

    /**
     * check the input path is valid(in the log directories of configuration)
     *
     * @param path file path
     * @return true/false
     */
    public static boolean checkValid(String path) {
        LogFileProperties properties = SpringUtils.get(LogFileProperties.class);
        return properties.getPath()
                .stream()
                .anyMatch(root -> Paths.get(path).toAbsolutePath().startsWith(Paths.get(root)));
    }

    /**
     * Splits log file by line, skip and take the special context.
     *
     * @param log  log file
     * @param skip skip count
     * @param take take count
     * @return log context order by line no asc
     * @throws IOException io exception
     */
    public static List<LogLineText> getLogText(File log, long skip, Integer take) throws IOException {
        if (!log.exists() || !log.isFile()) {
            return new ArrayList<>();
        }
        List<String> content = Files.lines(log.toPath(), LogProtocolCodec.CHARSET)
                .skip(skip)
                .limit(take)
                .collect(Collectors.toList());
        List<LogLineText> result = new ArrayList<>(content.size());
        for (int i = 0; i < content.size(); i++) {
            result.add(new LogLineText(skip + i + 1, content.get(i)));
        }
        return result;
    }
}
