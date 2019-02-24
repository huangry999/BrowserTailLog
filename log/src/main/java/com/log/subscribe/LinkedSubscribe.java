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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LinkedSubscribe{");
        sb.append("file=").append(file.getAbsoluteFile());
        sb.append(", channelIsActive=").append(context.channel().isActive());
        sb.append(", remoteAddress=").append(context.channel().remoteAddress());
        sb.append(", readIndex=").append(readIndex);
        sb.append('}');
        return sb.toString();
    }
}
