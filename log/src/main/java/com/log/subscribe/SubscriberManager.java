package com.log.subscribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Component
public class SubscriberManager {
    private static final Logger logger = LoggerFactory.getLogger(SubscriberManager.class);
    //TODO 释放无效的订阅者
    private List<Subscriber> subscribers = new ArrayList<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public void subscribe(Subscriber subscriber) {
        //TODO 偏向写锁
        lock.writeLock().lock();
        subscribers.add(subscriber);
        lock.writeLock().unlock();
    }

    /**
     * get subscriber event listener
     *
     * @param log the log file
     * @param kind event kind
     * @return listener of the event kind
     */
    public List<SubscribeEventHandler> getListener(File log, WatchEvent.Kind kind) {
        lock.readLock().lock();
        List<SubscribeEventHandler> r = subscribers
                .stream()
                .filter(s -> s.getLog().toPath().equals(log.toPath()))
                .map(s -> {
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        return s.getCreateHandler();
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        return s.getDeleteHandler();
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        return s.getModifyHandler();
                    } else {
                        logger.error("Not support event kind {}", kind);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        lock.readLock().unlock();
        return r;
    }
}
