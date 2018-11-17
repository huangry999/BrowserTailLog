package com.log.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

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
     * @param directory target directory
     * @param recursive is recursive
     * @param fileFilter filter, nullable
     * @return the files of this directory
     */
    public static List<File> listFiles(File directory, boolean recursive, FileFilter fileFilter){
        List<File> result = new ArrayList<>();
        if (directory == null || !directory.exists() || !directory.isDirectory()){
            return result;
        }
        for(File f: directory.listFiles()){
            if (f.isDirectory() && recursive){
                result.addAll(listFiles(f, true, fileFilter));
            }else if (fileFilter == null || fileFilter.accept(f)){
                result.add(f);
            }
        }
        return result;
    }
}
