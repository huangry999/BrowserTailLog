package com.log.uiapi.config;

import com.log.uiapi.config.bean.Host;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "uiapi-properties")
@Component
@Setter
@Getter
public class HostsProperties {
    private List<Host> hosts;
}