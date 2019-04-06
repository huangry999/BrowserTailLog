package com.log.fileservice.config;

import com.log.fileservice.config.bean.Path;
import lombok.Data;
import org.apache.commons.io.filefilter.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "log-file")
@Component
@Data
public class LogFileProperties {
    private List<Path> path = new ArrayList<>();
    private List<String> suffix = new ArrayList<>();
    private boolean recursive = true;

    public IOFileFilter getFilter() {
        IOFileFilter filter = TrueFileFilter.INSTANCE;
        if (!suffix.isEmpty()) {
            filter = new OrFileFilter(
                    this.suffix
                            .stream()
                            .filter(Strings::isNotBlank)
                            .map(SuffixFileFilter::new)
                            .collect(Collectors.toList()));
        }
        if (this.recursive) {
            filter = new OrFileFilter(filter, DirectoryFileFilter.DIRECTORY);
        } else {
            filter = new AndFileFilter(FileFileFilter.FILE, filter);
        }
        return filter;
    }
}
