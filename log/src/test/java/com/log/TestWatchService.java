package com.log;

import java.io.File;
import java.nio.file.*;

public class TestWatchService {

    public static void main(String[] args) throws Exception {
        WatchService service = FileSystems.getDefault().newWatchService();
        new File("G:\\log").toPath()
                .register(service, StandardWatchEventKinds.ENTRY_MODIFY);
        while (true) {
            WatchKey key = service.take();
            for (WatchEvent event : key.pollEvents()) {
                System.out.println(event.context() + " " + event.kind());
            }
            key.reset();
        }
    }
}
