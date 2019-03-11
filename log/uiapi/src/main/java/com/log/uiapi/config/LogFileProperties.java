package com.log.uiapi.config;

import org.apache.commons.io.filefilter.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "log")
@Component
public class LogFileProperties {
    private List<String> path = new ArrayList<>();
    private List<String> suffix = new ArrayList<>();
    private boolean recursive = true;

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<String> getSuffix() {
        return suffix;
    }

    public void setSuffix(List<String> suffix) {
        this.suffix = suffix;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public IOFileFilter getFilter() {
        IOFileFilter filter = new OrFileFilter(
                this.suffix
                        .stream()
                        .map(SuffixFileFilter::new)
                        .collect(Collectors.toList()));
        if (this.recursive) {
            filter = new OrFileFilter(filter, DirectoryFileFilter.DIRECTORY);
        } else {
            filter = new AndFileFilter(FileFileFilter.FILE, filter);
        }
        return filter;
    }
}
