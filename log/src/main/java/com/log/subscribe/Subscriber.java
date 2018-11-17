package com.log.subscribe;

import java.io.File;

public class Subscriber {
    private File log;
    private SubscribeEventHandler modifyHandler;
    private SubscribeEventHandler deleteHandler;
    private SubscribeEventHandler createHandler;

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

    public Subscriber(File log) {
        this.log = log;
    }

    public File getLog() {
        return log;
    }

}
