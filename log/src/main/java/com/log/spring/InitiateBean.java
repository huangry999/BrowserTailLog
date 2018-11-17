package com.log.spring;

import com.log.logmonitor.monitor.MonitorFactory;
import com.log.logmonitor.commoniomonitor.CommonIoMonitorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitiateBean {

    @Bean
    public MonitorFactory monitorFactory(){
        return new CommonIoMonitorFactory();
    }
}
