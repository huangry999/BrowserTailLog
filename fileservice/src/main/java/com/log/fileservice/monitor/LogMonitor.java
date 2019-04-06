package com.log.fileservice.monitor;

import com.log.fileservice.config.LogFileProperties;
import com.log.fileservice.config.bean.Path;
import com.log.fileservice.monitor.monitor.Monitor;
import com.log.fileservice.monitor.monitor.MonitorFactory;
import com.log.fileservice.monitor.monitor.MonitorParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Slf4j
public class LogMonitor {
    private final Monitor monitor;

    @Autowired
    public LogMonitor(MonitorFactory factory, LogFileProperties logFileProperties) {
        MonitorParameter parameter = new MonitorParameter();
        parameter.setRoots(logFileProperties.getPath().stream().map(Path::getPath).collect(Collectors.toList()));
        parameter.setFileFilter(logFileProperties.getFilter());
        monitor = factory.newMonitor(parameter);
    }

    /**
     * start to monitor async
     */
    public void startAsync() {
        try {
            this.monitor.start();
            log.info("monitor start successfully");

        } catch (Exception e) {
            log.error("start monitor error. ", e);
        }
    }

    public void destroy() {
        try {
            this.monitor.release();
        } catch (Exception e) {
            log.error("monitor destroy error", e);
        }
    }
}
