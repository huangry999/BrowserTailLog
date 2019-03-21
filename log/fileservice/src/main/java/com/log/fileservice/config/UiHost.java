package com.log.fileservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "ui-host")
@Component
@Getter
@Setter
public class UiHost {
    private String host;
    private int port;
}
