package com.log.fileservice.monitor;

import com.log.fileservice.config.LogFileProperties;
import com.log.fileservice.config.bean.Path;
import com.log.fileservice.grpc.EventType;
import com.log.fileservice.grpc.FileEventNotification;
import com.log.fileservice.grpc.FileType;
import com.log.fileservice.monitor.monitor.Monitor;
import com.log.fileservice.monitor.monitor.MonitorFactory;
import com.log.fileservice.monitor.monitor.MonitorListener;
import com.log.fileservice.monitor.monitor.MonitorParameter;
import com.log.fileservice.notify.NotificationService;
import com.log.fileservice.service.LogFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LogMonitor {
    private final ExecutorService executorService;
    private final Monitor monitor;
    @Value("${log-host.name}")
    private String hostName;
    private final NotificationService notificationService;

    /**
     * release resources
     */
    public void destroy() {
        if (this.executorService != null) {
            this.executorService.shutdown();
        }
        this.notificationService.destroy();
    }

    @Autowired
    public LogMonitor(MonitorFactory factory, LogFileProperties logFileProperties, LogFileService logFileService, NotificationService notificationService) {
        executorService = Executors.newCachedThreadPool();
        MonitorParameter parameter = new MonitorParameter();
        parameter.setRoots(logFileProperties.getPath().stream().map(Path::getPath).collect(Collectors.toList()));
        parameter.setFileFilter(logFileProperties.getFilter());
        parameter.setMonitorListener(new MonitorListener() {
            @Override
            public void onDirectoryCreate(File directory) {
                log.debug("directory create event: {}", directory.getAbsolutePath());
                String aliasPath = logFileService.convertToAliasPath(directory.getAbsolutePath());
                if (Strings.isNotBlank(aliasPath)) {
                    FileEventNotification notification = FileEventNotification.newBuilder()
                            .setFilePath(aliasPath)
                            .setEventType(EventType.CREATE)
                            .setFileType(FileType.DIRECTORY)
                            .setFromHost(hostName)
                            .build();
                    notificationService.sendNotification(notification);
                }
            }

            @Override
            public void onDirectoryChange(File directory) {
                log.debug("directory change event: {}", directory.getAbsolutePath());
                String aliasPath = logFileService.convertToAliasPath(directory.getAbsolutePath());
                if (Strings.isNotBlank(aliasPath)) {
                    FileEventNotification notification = FileEventNotification.newBuilder()
                            .setFilePath(aliasPath)
                            .setEventType(EventType.MODIFY)
                            .setFileType(FileType.DIRECTORY)
                            .setFromHost(hostName)
                            .build();
                    notificationService.sendNotification(notification);
                }
            }

            @Override
            public void onDirectoryDelete(File directory) {
                log.debug("file delete event: {}", directory.getAbsolutePath());
                String aliasPath = logFileService.convertToAliasPath(directory.getAbsolutePath());
                if (Strings.isNotBlank(aliasPath)) {
                    FileEventNotification notification = FileEventNotification.newBuilder()
                            .setFilePath(aliasPath)
                            .setEventType(EventType.DELETE)
                            .setFileType(FileType.DIRECTORY)
                            .setFromHost(hostName)
                            .build();
                    notificationService.sendNotification(notification);
                }
            }

            @Override
            public void onFileCreate(File file) {
                log.debug("file create event: {}", file.getAbsolutePath());
                String aliasPath = logFileService.convertToAliasPath(file.getAbsolutePath());
                if (Strings.isNotBlank(aliasPath)) {
                    FileEventNotification notification = FileEventNotification.newBuilder()
                            .setFilePath(aliasPath)
                            .setEventType(EventType.CREATE)
                            .setFileType(FileType.FILE)
                            .setFromHost(hostName)
                            .build();
                    notificationService.sendNotification(notification);
                }
            }

            @Override
            public void onFileModify(File file) {
                log.debug("file modify event: {}", file.getAbsolutePath());
                String aliasPath = logFileService.convertToAliasPath(file.getAbsolutePath());
                boolean refresh = logFileService.updateFileContext(file);
                if (Strings.isNotBlank(aliasPath)) {
                    FileEventNotification notification = FileEventNotification.newBuilder()
                            .setFilePath(aliasPath)
                            .setEventType(EventType.MODIFY)
                            .setFileType(FileType.FILE)
                            .setFromHost(hostName)
                            .setRefresh(refresh)
                            .build();
                    notificationService.sendNotification(notification);
                }
            }

            @Override
            public void onFileDelete(File file) {
                log.debug("file delete event: {}", file.getAbsolutePath());
                String aliasPath = logFileService.convertToAliasPath(file.getAbsolutePath());
                logFileService.removeFileContext(file);
                if (Strings.isNotBlank(aliasPath)) {
                    FileEventNotification notification = FileEventNotification.newBuilder()
                            .setFilePath(aliasPath)
                            .setEventType(EventType.DELETE)
                            .setFileType(FileType.FILE)
                            .setFromHost(hostName)
                            .build();
                    notificationService.sendNotification(notification);
                }
            }
        });
        monitor = factory.newMonitor(parameter);
        this.notificationService = notificationService;
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
}
