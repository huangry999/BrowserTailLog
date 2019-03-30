package com.log.fileservice.config.bean;

import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;

import javax.validation.constraints.NotNull;

@Data
public class Path {
    @NotNull
    private String path;
    private String alias;

    public String getAlias() {
        if (Strings.isNotBlank(alias)) {
            return alias;
        }
        return FilenameUtils.getName(path);
    }
}