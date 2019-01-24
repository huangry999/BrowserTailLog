package com.log.logmonitor;

import com.log.config.LogFileProperties;
import com.log.logmonitor.monitor.Monitor;
import com.log.logmonitor.monitor.MonitorFactory;
import com.log.logmonitor.monitor.MonitorListener;
import com.log.logmonitor.monitor.MonitorParameter;
import com.log.subscribe.Subscriber;
import com.log.subscribe.SubscriberManager;
import org.apache.commons.io.filefilter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class LogMonitor {
    private final static Logger logger = LoggerFactory.getLogger(LogMonitor.class);
    private ExecutorService executorService;
    private Monitor monitor;

    /**
     * release resources
     */
    public void destroy() {
        if (this.executorService != null) {
            this.executorService.shutdown();
        }
    }

    @Autowired
    public LogMonitor(SubscriberManager subscriberManager, MonitorFactory factory, LogFileProperties logFileProperties) {
        executorService = Executors.newCachedThreadPool();

        //init monitor
        MonitorParameter parameter = new MonitorParameter();
        parameter.setRoots(logFileProperties.getPath());
        parameter.setFileFilter(logFileProperties.getFilter());
        parameter.setMonitorListener(new MonitorListener() {
            @Override
            public void onDirectoryCreate(File directory) {
                for (Subscriber s : subscriberManager.getSubscribers(directory)) {
                    if (s.getCreateHandler() == null)
                        continue;
                    executorService.submit(() -> s.getCreateHandler().handle(s));
                }
                final File parent = directory.getParentFile();
                if (parent != null){
                    this.onDirectoryChange(parent);
                }
            }

            @Override
            public void onDirectoryChange(File directory) {
                for (Subscriber s : subscriberManager.getSubscribers(directory)) {
                    if (s.getModifyHandler() == null)
                        continue;
                    executorService.submit(() -> s.getModifyHandler().handle(s));
                }
            }

            @Override
            public void onDirectoryDelete(File directory) {
                for (Subscriber s : subscriberManager.getSubscribers(directory)) {
                    if (s.getDeleteHandler() == null)
                        continue;
                    executorService.submit(() -> s.getDeleteHandler().handle(s));
                }
                final File parent = directory.getParentFile();
                if (parent != null){
                    this.onDirectoryChange(parent);
                }
            }

            @Override
            public void onFileCreate(File file) {
                for (Subscriber s : subscriberManager.getSubscribers(file)) {
                    if (s.getCreateHandler() == null)
                        continue;
                    executorService.submit(() -> s.getCreateHandler().handle(s));
                }
            }

            @Override
            public void onFileModify(File file) {
                for (Subscriber s : subscriberManager.getSubscribers(file)) {
                    if (s.getModifyHandler() == null)
                        continue;
                    executorService.submit(() -> s.getModifyHandler().handle(s));
                }
                final File parent = file.getParentFile();
                if (parent != null){
                    this.onDirectoryChange(parent);
                }
            }

            @Override
            public void onFileDelete(File file) {
                for (Subscriber s : subscriberManager.getSubscribers(file)) {
                    if (s.getDeleteHandler() == null)
                        continue;
                    executorService.submit(() -> s.getDeleteHandler().handle(s));
                }
            }
        });
        monitor = factory.newMonitor(parameter);
    }

    /**
     * start to monitor async
     */
    public void startAsync() {
        try {
            this.monitor.start();
            logger.info("monitor start successfully");
        } catch (Exception e) {
            logger.error("start monitor error. ", e);
        }
    }
}
