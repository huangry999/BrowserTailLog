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
