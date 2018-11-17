package com.log.subscribe;

import java.io.File;

public interface SubscribeEventHandler {
    void handle(File file);
}
