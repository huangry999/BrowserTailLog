package com.log.logmonitor;

import com.log.config.LogFileProperties;
import com.log.logmonitor.monitor.Monitor;
import com.log.logmonitor.monitor.MonitorFactory;
import com.log.logmonitor.monitor.MonitorListener;
import com.log.logmonitor.monitor.MonitorParameter;
import com.log.subscribe.SubscribeEventHandler;
import com.log.subscribe.SubscriberManager;
import com.log.util.SpringUtils;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class LogMonitor {
    private final static Logger logger = LoggerFactory.getLogger(LogMonitor.class);
    private ExecutorService subscribeExecutorService;
    private SubscriberManager subscriberManager;
    private Monitor monitor;

    /**
     * release resources
     */
    public void destroy() {
        if (this.subscribeExecutorService != null) {
            this.subscribeExecutorService.shutdown();
        }
    }

    public LogMonitor() {
        subscribeExecutorService = Executors.newCachedThreadPool();
        subscriberManager = SpringUtils.get(SubscriberManager.class);
        LogFileProperties logFileProperties = SpringUtils.get(LogFileProperties.class);
        MonitorFactory factory = SpringUtils.get(MonitorFactory.class);

        //init monitor
        MonitorParameter parameter = new MonitorParameter();
        parameter.setRoots(logFileProperties.getPath());
        parameter.setFileFilter(new OrFileFilter(
                logFileProperties.getSuffix()
                        .stream()
                        .map(SuffixFileFilter::new)
                        .collect(Collectors.toList())));
        parameter.setMonitorListener(new MonitorListener() {
            @Override
            public void onDirectoryCreate(File directory) {

            }

            @Override
            public void onDirectoryChange(File directory) {

            }

            @Override
            public void onDirectoryDelete(File directory) {

            }

            @Override
            public void onFileCreate(File file) {
                for (SubscribeEventHandler handler : subscriberManager.getListener(file, StandardWatchEventKinds.ENTRY_CREATE)) {
                    subscribeExecutorService.submit(() -> handler.handle(file));
                }
            }

            @Override
            public void onFileChange(File file) {
                for (SubscribeEventHandler handler : subscriberManager.getListener(file, StandardWatchEventKinds.ENTRY_MODIFY)) {
                    subscribeExecutorService.submit(() -> handler.handle(file));
                }
            }

            @Override
            public void onFileDelete(File file) {
                for (SubscribeEventHandler handler : subscriberManager.getListener(file, StandardWatchEventKinds.ENTRY_DELETE)) {
                    subscribeExecutorService.submit(() -> handler.handle(file));
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
        } catch (Exception e) {
            logger.error("start monitor error. ", e);
        }
    }
}
