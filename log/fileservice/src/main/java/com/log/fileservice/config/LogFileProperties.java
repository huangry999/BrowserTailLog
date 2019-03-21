package com.log.fileservice.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.filefilter.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "log-file")
@Component
@Getter
@Setter
public class LogFileProperties {
    private List<String> path = new ArrayList<>();
    private List<String> suffix = new ArrayList<>();
    private boolean recursive = true;

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
