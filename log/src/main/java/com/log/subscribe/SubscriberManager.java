package com.log.subscribe;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class SubscriberManager {
    private static final Logger logger = LoggerFactory.getLogger(SubscriberManager.class);
    private List<Subscriber> subscribers = new CopyOnWriteArrayList<>();

    /**
     * Remove all subscribes belong to socket of context
     *
     * @param ctx context
     */
    public void remove(ChannelHandlerContext ctx) {
        List<Subscriber> rm = subscribers.stream().filter(s -> s.getContext().equals(ctx)).collect(Collectors.toList());
        subscribers.removeAll(rm);
    }

    /**
     * Remove special log from socket of context
     *
     * @param ctx context
     * @param log log file
     */
    public void remove(ChannelHandlerContext ctx, File log) {
        subscribers
                .stream()
                .filter(s -> s.getContext().equals(ctx))
                .filter(s -> s.getLog().equals(log))
                .findAny()
                .ifPresent(s -> subscribers.remove(s));
    }

    /**
     * Add a subscriber
     *
     * @param subscriber the new subscriber
     */
    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * get subscriber by event and log
     *
     * @param log the log file
     * @return subscribers
     */
    public List<Subscriber> getSubscribers(File log) {
        List<Subscriber> r = subscribers.stream()
                .filter(s -> s.getLog().equals(log))
                .filter(s -> s.getContext().channel().isActive())
                .collect(Collectors.toList());
        return r;
    }
}
