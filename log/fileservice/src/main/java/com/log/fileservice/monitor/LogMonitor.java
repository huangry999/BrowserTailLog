package com.log.fileservice.monitor;

import com.log.fileservice.config.LogFileProperties;
import com.log.fileservice.config.UiHost;
import com.log.fileservice.grpc.EventType;
import com.log.fileservice.grpc.FileEventNotification;
import com.log.fileservice.grpc.FileEventNotificationServiceGrpc;
import com.log.fileservice.grpc.FileType;
import com.log.fileservice.monitor.monitor.Monitor;
import com.log.fileservice.monitor.monitor.MonitorFactory;
import com.log.fileservice.monitor.monitor.MonitorListener;
import com.log.fileservice.monitor.monitor.MonitorParameter;
import com.log.fileservice.reader.LogFileService;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class LogMonitor {
    private final ExecutorService executorService;
    private final Monitor monitor;
    private final ManagedChannel managedChannel;

    /**
     * release resources
     */
    public void destroy() {
        if (this.executorService != null) {
            this.executorService.shutdown();
        }
        try {
            managedChannel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("shutdown gprc managedChannel error", e);
        }
    }

    @Autowired
    public LogMonitor(MonitorFactory factory, LogFileProperties logFileProperties, LogFileService logFileService, UiHost uiHost) {
        executorService = Executors.newCachedThreadPool();
        MonitorParameter parameter = new MonitorParameter();
        parameter.setRoots(logFileProperties.getPath());
        parameter.setFileFilter(logFileProperties.getFilter());
        managedChannel = NettyChannelBuilder.forAddress(uiHost.getHost(), uiHost.getPort())
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        FileEventNotificationServiceGrpc.FileEventNotificationServiceFutureStub futureStub = FileEventNotificationServiceGrpc.newFutureStub(managedChannel);
        parameter.setMonitorListener(new MonitorListener() {
            @Override
            public void onDirectoryCreate(File directory) {
                log.debug("directory create event: {}", directory.getAbsolutePath());
                FileEventNotification notification = FileEventNotification.newBuilder()
                        .setFilePath(directory.getAbsolutePath())
                        .setEventType(EventType.CREATE)
                        .setFileType(FileType.DIRECTORY)
                        .build();
                futureStub.notify(notification);
            }

            @Override
            public void onDirectoryChange(File directory) {
                log.debug("directory change event: {}", directory.getAbsolutePath());
                FileEventNotification notification = FileEventNotification.newBuilder()
                        .setFilePath(directory.getAbsolutePath())
                        .setEventType(EventType.MODIFY)
                        .setFileType(FileType.DIRECTORY)
                        .build();
                futureStub.notify(notification);
            }

            @Override
            public void onDirectoryDelete(File directory) {
                log.debug("file delete event: {}", directory.getAbsolutePath());
                FileEventNotification notification = FileEventNotification.newBuilder()
                        .setFilePath(directory.getAbsolutePath())
                        .setEventType(EventType.DELETE)
                        .setFileType(FileType.DIRECTORY)
                        .build();
                futureStub.notify(notification);
            }

            @Override
            public void onFileCreate(File file) {
                log.debug("file create event: {}", file.getAbsolutePath());
                logFileService.removeFileContext(file);
                FileEventNotification notification = FileEventNotification.newBuilder()
                        .setFilePath(file.getAbsolutePath())
                        .setEventType(EventType.CREATE)
                        .setFileType(FileType.FILE)
                        .build();
                futureStub.notify(notification);
            }

            @Override
            public void onFileModify(File file) {
                log.debug("file modify event: {}", file.getAbsolutePath());
                FileEventNotification notification = FileEventNotification.newBuilder()
                        .setFilePath(file.getAbsolutePath())
                        .setEventType(EventType.MODIFY)
                        .setFileType(FileType.FILE)
                        .build();
                futureStub.notify(notification);
            }

            @Override
            public void onFileDelete(File file) {
                log.debug("file delete event: {}", file.getAbsolutePath());
                logFileService.removeFileContext(file);
                FileEventNotification notification = FileEventNotification.newBuilder()
                        .setFilePath(file.getAbsolutePath())
                        .setEventType(EventType.DELETE)
                        .setFileType(FileType.FILE)
                        .build();
                futureStub.notify(notification);
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
}
