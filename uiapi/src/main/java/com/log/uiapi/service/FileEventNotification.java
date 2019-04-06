package com.log.uiapi.service;

import com.log.fileservice.grpc.Empty;
import com.log.fileservice.grpc.EventType;
import com.log.fileservice.grpc.FileEventNotificationServiceGrpc;
import com.log.fileservice.grpc.FileType;
import com.log.uiapi.subscribe.Subscriber;
import com.log.uiapi.subscribe.SubscriberManager;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@GRpcService
@Service
@Slf4j
public class FileEventNotification extends FileEventNotificationServiceGrpc.FileEventNotificationServiceImplBase {
    private final SubscriberManager subscriberManager;

    @Autowired
    public FileEventNotification(SubscriberManager subscriberManager) {
        this.subscriberManager = subscriberManager;
    }

    @Override
    public void notify(com.log.fileservice.grpc.FileEventNotification request, StreamObserver<Empty> responseObserver) {
        EventType eventType = request.getEventType();
        String filePath = request.getFilePath();
        String hostName = request.getFromHost();
        FileType fileType = request.getFileType();
        List<Subscriber> subscribers = subscriberManager.getSubscribers(filePath, hostName);
        log.debug("receive file event: {}, file: [{}]{}, host: {}", eventType, fileType, filePath, hostName);
        for (Subscriber s : subscribers) {
            switch (eventType) {
                case CREATE:
                    s.notifyCreate();
                    break;
                case DELETE:
                    s.notifyDelete();
                    break;
                case MODIFY:
                    // if index has been refresh, call creating method
                    if (request.getRefresh()) {
                        s.notifyCreate();
                    } else {
                        s.notifyModify();
                    }
                    break;
                default:
                    throw new RuntimeException("unknown event type: " + eventType);
            }
        }
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
