package com.log.uiapi.subscribe;

import java.io.File;

public class Subscriber {
    protected final File file;
    protected final String hostName;
    protected SubscribeEventHandler modifyHandler;
    protected SubscribeEventHandler deleteHandler;
    protected SubscribeEventHandler createHandler;

    public Subscriber(File file, String hostName) {
        this.file = file;
        this.hostName = hostName;
    }

    @Override
    public String toString() {
        return "Subscriber{" +
                "file=" + file +
                ", hostName='" + hostName + '\'' +
                '}';
    }

    public void notifyDelete() {
        if (deleteHandler != null) {
            deleteHandler.handle(this);
        }
    }

    public void notifyCreate() {
        if (createHandler != null) {
            createHandler.handle(this);
        }
    }

    public void notifyModify() {
        if (modifyHandler != null) {
            modifyHandler.handle(this);
        }
    }

    public void setModifyHandler(SubscribeEventHandler modifyHandler) {
        this.modifyHandler = modifyHandler;
    }

    public void setDeleteHandler(SubscribeEventHandler deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

    public void setCreateHandler(SubscribeEventHandler createHandler) {
        this.createHandler = createHandler;
    }

    public File getFile() {
        return file;
    }

    public String getHostName() {
        return hostName;
    }
}
