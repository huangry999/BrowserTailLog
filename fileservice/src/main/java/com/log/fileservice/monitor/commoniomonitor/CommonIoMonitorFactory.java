package com.log.fileservice.monitor.commoniomonitor;

import com.log.fileservice.grpc.EventType;
import com.log.fileservice.grpc.FileType;
import com.log.fileservice.monitor.monitor.FileEvent;
import com.log.fileservice.monitor.monitor.Monitor;
import com.log.fileservice.monitor.monitor.MonitorFactory;
import com.log.fileservice.monitor.monitor.MonitorParameter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CommonIoMonitorFactory implements MonitorFactory {
    @Value("${file-monitor.interval-ms:3000}")
    private int intervalMs;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public CommonIoMonitorFactory(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Monitor newMonitor(MonitorParameter parameter) {
        return new CommonIoMonitor(parameter, intervalMs, applicationEventPublisher);
    }
}

class CommonIoMonitor implements Monitor {
    private FileAlterationMonitor monitor;
    private static final Logger logger = LoggerFactory.getLogger(CommonIoMonitorFactory.class);

    CommonIoMonitor(MonitorParameter parameter, int intervalMs, ApplicationEventPublisher eventPublisher) {
        monitor = new FileAlterationMonitor(intervalMs);
        if (parameter.getRoots() != null && !parameter.getRoots().isEmpty())
            for (String path : parameter.getRoots()) {
                FileAlterationObserver observer = new FileAlterationObserver(path, parameter.getFileFilter());
                observer.addListener(new FileAlterationListener() {
                    @Override
                    public void onStart(FileAlterationObserver observer) {
                    }

                    @Override
                    public void onDirectoryCreate(File directory) {
                        eventPublisher.publishEvent(new FileEvent(FileType.DIRECTORY, directory, EventType.CREATE));
                    }

                    @Override
                    public void onDirectoryChange(File directory) {
                        eventPublisher.publishEvent(new FileEvent(FileType.DIRECTORY, directory, EventType.MODIFY));
                    }

                    @Override
                    public void onDirectoryDelete(File directory) {
                        eventPublisher.publishEvent(new FileEvent(FileType.DIRECTORY, directory, EventType.DELETE));
                    }

                    @Override
                    public void onFileCreate(File file) {
                        eventPublisher.publishEvent(new FileEvent(FileType.FILE, file, EventType.CREATE));
                        if (path.equals(file.getParent())) {
                            this.onDirectoryChange(file.getParentFile());
                        }
                    }

                    @Override
                    public void onFileChange(File file) {
                        if (file.exists()) {
                            eventPublisher.publishEvent(new FileEvent(FileType.FILE, file, EventType.MODIFY));
                        } else {
                            this.onFileDelete(file);
                        }
                    }

                    @Override
                    public void onFileDelete(File file) {
                        eventPublisher.publishEvent(new FileEvent(FileType.FILE, file, EventType.DELETE));
                        if (path.equals(file.getParent())) {
                            this.onDirectoryChange(file.getParentFile());
                        }
                    }

                    @Override
                    public void onStop(FileAlterationObserver observer) {

                    }
                });
                this.monitor.addObserver(observer);
                logger.debug("add {} to monitor", path);

            }
    }

    @Override
    public void start() throws Exception {
        this.monitor.start();
        logger.debug("monitor start");
    }

    @Override
    public void pause() throws Exception {
        this.monitor.stop();
    }

    @Override
    public void release() throws Exception {
        this.monitor.stop();
    }
}
