package com.log.subscribe;

import io.netty.channel.ChannelHandlerContext;

import java.io.File;

public class Subscriber {
    private final File file;
    private long readIndex;
    private final ChannelHandlerContext context;

    private SubscribeEventHandler modifyHandler;
    private SubscribeEventHandler deleteHandler;
    private SubscribeEventHandler createHandler;

    public Subscriber(File file, ChannelHandlerContext context) {
        this.file = file;
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

    public SubscribeEventHandler getModifyHandler() {
        return modifyHandler;
    }

    public void setModifyHandler(SubscribeEventHandler modifyHandler) {
        this.modifyHandler = modifyHandler;
    }

    public SubscribeEventHandler getDeleteHandler() {
        return deleteHandler;
    }

    public void setDeleteHandler(SubscribeEventHandler deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

    public SubscribeEventHandler getCreateHandler() {
        return createHandler;
    }

    public void setCreateHandler(SubscribeEventHandler createHandler) {
        this.createHandler = createHandler;
    }

    public File getFile() {
        return file;
    }

}
