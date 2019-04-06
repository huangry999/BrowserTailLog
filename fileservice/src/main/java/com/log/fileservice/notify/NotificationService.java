package com.log.fileservice.notify;

import com.log.fileservice.grpc.FileEventNotification;
import com.log.fileservice.grpc.FileEventNotificationServiceGrpc;
import com.log.fileservice.monitor.monitor.FileEvent;
import com.log.fileservice.service.LogFileService;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class NotificationService {
    @Value("${eureka.ui-app-name:uiapi}")
    private String uiAppName;
    private ManagedChannel uiChannel;
    private final DiscoveryClient discoveryClient;
    private final LogFileService fileService;
    @Value("${log-host.name}")
    private String hostName;

    @Autowired
    public NotificationService(DiscoveryClient discoveryClient, LogFileService fileService) {
        this.discoveryClient = discoveryClient;
        this.fileService = fileService;
    }

    @Async
    @EventListener
    public void fileEventListener(FileEvent event) {
        log.debug("file event occur. file: {}, event: ", event.getFile().getPath(), event.getEventType());
        String alias = fileService.convertToAliasPath(event.getFile().getPath());
        if (Strings.isBlank(alias)) {
            return;
        }
        FileEventNotification notification = FileEventNotification.newBuilder()
                .setFilePath(alias)
                .setEventType(event.getEventType())
                .setFileType(event.getFileType())
                .setFromHost(hostName)
                .build();
        this.sendNotification(notification);
    }

    /**
     * send notification to all ui service
     *
     * @param notification notification to send
     */
    private void sendNotification(FileEventNotification notification) {
        ServiceInstance uiInstance = null;
        if (discoveryClient.getInstances(uiAppName) != null &&
                !discoveryClient.getInstances(uiAppName).isEmpty()) {
            uiInstance = discoveryClient.getInstances(uiAppName).get(0);
        }
        if (uiInstance == null) {
            log.info("ui service is off line");
            return;
        }
        if (uiChannel == null) {
            synchronized (this) {
                if (uiChannel == null) {
                    log.info("init ui service channel");
                    uiChannel = NettyChannelBuilder.forAddress(uiInstance.getHost(), uiInstance.getPort())
                            .negotiationType(NegotiationType.PLAINTEXT)
                            .build();
                }
            }
        }
        try {
            FileEventNotificationServiceGrpc.newFutureStub(uiChannel).notify(notification);
        } catch (Exception e) {
            log.error("notify exception, set ui channel to ne null", e);
        }
    }

    public void destroy() {
        try {
            uiChannel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("shutdown gprc managedChannel error", e);
        }
    }
}
