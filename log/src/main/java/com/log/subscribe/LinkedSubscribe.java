package com.log.subscribe;

import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class LinkedSubscribe extends Subscriber {
    private final ChannelHandlerContext context;
    private long readIndex;
    private final Lock lock = new ReentrantLock();

    public LinkedSubscribe(File file, ChannelHandlerContext context) {
        super(file);
        this.context = context;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public long getReadIndex() {
        return readIndex;
    }

    public void setReadIndex(long readIndex) {
        this.readIndex = readIndex;
    }

    public Lock getLock() {
        return lock;
    }
}
