package com.log.fileservice.notify;

import com.log.fileservice.grpc.FileEventNotification;
import com.log.fileservice.grpc.FileEventNotificationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class NotificationService {
    @Value("${eureka.ui-app-name:uiapi}")
    private String uiAppName;
    private ManagedChannel uiChannel;
    private final DiscoveryClient discoveryClient;

    @Autowired
    public NotificationService(DiscoveryClient discoveryClient, LogFileService fileService) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * send notification to all ui service
     *
     * @param notification notification to send
     */
    public void sendNotification(FileEventNotification notification) {
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
