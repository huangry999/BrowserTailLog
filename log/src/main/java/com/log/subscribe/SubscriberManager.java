package com.log.subscribe;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
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
     * @param ctx  context
     * @param file the file
     */
    public void remove(ChannelHandlerContext ctx, File file) {
        subscribers
                .stream()
                .filter(s -> s.getContext().equals(ctx))
                .filter(s -> s.getFile().equals(file))
                .findAny()
                .ifPresent(s -> subscribers.remove(s));
    }

    /**
     * Remove files from socket of context by filter
     *
     * @param ctx    context
     * @param filter file filter
     */
    public void remove(ChannelHandlerContext ctx, FileFilter filter) {
        subscribers
                .stream()
                .filter(s -> s.getContext().equals(ctx))
                .filter(s -> filter.accept(s.getFile()))
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
     * @param file the file
     * @return subscribers
     */
    public List<Subscriber> getSubscribers(File file) {
        List<Subscriber> r = subscribers.stream()
                .filter(s -> s.getFile().equals(file))
                .filter(s -> s.getContext().channel().isActive())
                .collect(Collectors.toList());
        return r;
    }
}
