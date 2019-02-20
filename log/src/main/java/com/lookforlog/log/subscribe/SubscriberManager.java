package com.lookforlog.log.subscribe;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class SubscriberManager implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(SubscriberManager.class);
    private List<Subscriber> subscribers = new CopyOnWriteArrayList<>();

    /**
     * Remove all subscribes belong to socket of context
     *
     * @param ctx context
     */
    public void remove(ChannelHandlerContext ctx) {
        List<Subscriber> rm = subscribers.stream()
                .filter(s -> s instanceof LinkedSubscribe)
                .map(s -> (LinkedSubscribe) s)
                .filter(s -> s.getContext().equals(ctx)).collect(Collectors.toList());
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
                .filter(s -> s instanceof LinkedSubscribe)
                .map(s -> (LinkedSubscribe) s)
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
                .filter(s -> s instanceof LinkedSubscribe)
                .map(s -> (LinkedSubscribe) s)
                .filter(s -> s.getContext().equals(ctx))
                .filter(s -> filter.accept(s.getFile()))
                .findAny()
                .ifPresent(s -> subscribers.remove(s));
    }

    /**
     * remove special subscriber
     *
     * @param subscriber subscriber
     */
    public void remove(Subscriber subscriber) {
        subscribers.remove(subscriber);
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
                .collect(Collectors.toList());
        return r;
    }

    @Override
    @NonNull
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Boolean enabledLog = applicationContext.getEnvironment().getProperty("system.subscribe.enabledLog", Boolean.class);
        Integer logSamplingMin = applicationContext.getEnvironment().getProperty("system.subscribe.logSamplingMin", Integer.class);
        if (enabledLog == Boolean.TRUE) {
            Timer timer = new Timer(true);
            assert Objects.nonNull(logSamplingMin);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    List<Subscriber> copy = new ArrayList<>(subscribers);
                    Map<File, List<Subscriber>> fileStats = copy.stream().collect(Collectors.groupingBy(Subscriber::getFile));
                    StringBuilder sb = new StringBuilder("SubscriberManager Statistics---------------\n");
                    sb.append("---------------total subscribe size: ").append(copy.size()).append("\n");
                    for (File file : fileStats.keySet()) {
                        sb.append(fileStats.get(file).size()).append(" subscribed file ").append(file.getAbsolutePath()).append('\n');
                    }
                    sb.append("---------------details ---------------\n");
                    for (Subscriber subscriber : copy) {
                        sb.append(subscriber).append('\n');
                    }
                    logger.info(sb.toString());
                }
            }, 0, logSamplingMin * 1000);
        }
    }
}
