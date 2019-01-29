package com.log.subscribe;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Subscriber {
    protected final File file;
    protected SubscribeEventHandler modifyHandler;
    protected SubscribeEventHandler deleteHandler;
    protected SubscribeEventHandler createHandler;

    public Subscriber(File file) {
        this.file = file;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Subscriber{");
        sb.append("file=").append(file.getAbsoluteFile());
        sb.append('}');
        return sb.toString();
    }
}
